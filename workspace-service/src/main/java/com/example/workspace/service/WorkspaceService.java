package com.example.workspace.service;

import com.example.common.exception.ResourceNotFoundException;
import com.example.workspace.dto.*;
import com.example.workspace.model.*;
import com.example.workspace.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceInvitationRepository invitationRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository,
                            WorkspaceMemberRepository memberRepository,
                            WorkspaceInvitationRepository invitationRepository) {
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.invitationRepository = invitationRepository;
    }

    @Transactional
    public Workspace createWorkspace(CreateWorkspaceRequest request) {
        String name = required(request.getName(), "Workspace name");
        String slug = normalizeSlug(request.getSlug());
        String owner = required(request.getOwnerUsername(), "Owner username");
        if (workspaceRepository.findBySlug(slug).isPresent()) {
            throw new IllegalArgumentException("Workspace slug '" + slug + "' already exists");
        }

        Workspace workspace = workspaceRepository.save(new Workspace(name, slug, request.getDescription(), owner));
        memberRepository.save(new WorkspaceMember(workspace.getId(), owner, WorkspaceRole.OWNER));
        return workspace;
    }

    public List<Workspace> getWorkspaces(String username) {
        if (username == null || username.trim().isEmpty()) {
            return workspaceRepository.findAll();
        }
        List<Long> workspaceIds = memberRepository.findByUsername(username.trim()).stream()
                .map(WorkspaceMember::getWorkspaceId)
                .collect(Collectors.toList());
        return workspaceIds.isEmpty() ? Collections.emptyList() : workspaceRepository.findByIdIn(workspaceIds);
    }

    public WorkspaceDetailDto getWorkspace(Long workspaceId) {
        Workspace workspace = findWorkspace(workspaceId);
        return new WorkspaceDetailDto(workspace,
                memberRepository.findByWorkspaceIdOrderByJoinedAtAsc(workspaceId));
    }

    @Transactional
    public Workspace updateWorkspace(Long workspaceId, UpdateWorkspaceRequest request) {
        Workspace workspace = findWorkspace(workspaceId);
        if (request.getName() != null) {
            workspace.setName(required(request.getName(), "Workspace name"));
        }
        if (request.getDescription() != null) {
            workspace.setDescription(request.getDescription().trim());
        }
        workspace.setUpdatedAt(LocalDateTime.now());
        return workspaceRepository.save(workspace);
    }

    @Transactional
    public Workspace archiveWorkspace(Long workspaceId) {
        Workspace workspace = findWorkspace(workspaceId);
        workspace.setStatus("ARCHIVED");
        workspace.setUpdatedAt(LocalDateTime.now());
        return workspaceRepository.save(workspace);
    }

    public List<WorkspaceMember> getMembers(Long workspaceId) {
        findWorkspace(workspaceId);
        return memberRepository.findByWorkspaceIdOrderByJoinedAtAsc(workspaceId);
    }

    @Transactional
    public WorkspaceMember addMember(Long workspaceId, MemberRequest request) {
        findWorkspace(workspaceId);
        String username = required(request.getUsername(), "Username");
        if (memberRepository.existsByWorkspaceIdAndUsername(workspaceId, username)) {
            throw new IllegalArgumentException("User is already a workspace member");
        }
        WorkspaceRole role = parseAssignableRole(request.getRole(), WorkspaceRole.MEMBER);
        return memberRepository.save(new WorkspaceMember(workspaceId, username, role));
    }

    @Transactional
    public WorkspaceMember updateMemberRole(Long workspaceId, String username, RoleRequest request) {
        WorkspaceMember member = findMember(workspaceId, username);
        if (member.getRole() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException("Owner role can only be changed by transferring ownership");
        }
        member.setRole(parseAssignableRole(request.getRole(), null));
        return memberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long workspaceId, String username) {
        WorkspaceMember member = findMember(workspaceId, username);
        if (member.getRole() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException("Workspace owner cannot be removed");
        }
        memberRepository.delete(member);
    }

    @Transactional
    public Workspace transferOwnership(Long workspaceId, String newOwnerUsername) {
        Workspace workspace = findWorkspace(workspaceId);
        WorkspaceMember currentOwner = findMember(workspaceId, workspace.getOwnerUsername());
        WorkspaceMember newOwner = findMember(workspaceId, required(newOwnerUsername, "New owner username"));
        if (currentOwner.getId().equals(newOwner.getId())) {
            return workspace;
        }
        currentOwner.setRole(WorkspaceRole.ADMIN);
        newOwner.setRole(WorkspaceRole.OWNER);
        memberRepository.save(currentOwner);
        memberRepository.save(newOwner);
        workspace.setOwnerUsername(newOwner.getUsername());
        workspace.setUpdatedAt(LocalDateTime.now());
        return workspaceRepository.save(workspace);
    }

    @Transactional
    public WorkspaceInvitation createInvitation(Long workspaceId, CreateInvitationRequest request) {
        Workspace workspace = findWorkspace(workspaceId);
        if (!"ACTIVE".equals(workspace.getStatus())) {
            throw new IllegalArgumentException("Cannot invite members to an archived workspace");
        }
        String email = normalizeEmail(request.getEmail());
        String invitedBy = required(request.getInvitedBy(), "Invited by");
        WorkspaceMember inviter = findMember(workspaceId, invitedBy);
        if (inviter.getRole() != WorkspaceRole.OWNER && inviter.getRole() != WorkspaceRole.ADMIN) {
            throw new IllegalArgumentException("Only workspace owners and admins can invite members");
        }
        if (invitationRepository.findByWorkspaceIdAndEmailAndStatus(
                workspaceId, email, InvitationStatus.PENDING).isPresent()) {
            throw new IllegalArgumentException("A pending invitation already exists for this email");
        }
        int hours = request.getExpiresInHours() == null ? 72 : request.getExpiresInHours();
        if (hours < 1 || hours > 720) {
            throw new IllegalArgumentException("Invitation expiry must be between 1 and 720 hours");
        }
        WorkspaceRole role = parseAssignableRole(request.getRole(), WorkspaceRole.MEMBER);
        return invitationRepository.save(new WorkspaceInvitation(workspaceId, email, invitedBy, role,
                UUID.randomUUID().toString(), LocalDateTime.now().plusHours(hours)));
    }

    public List<WorkspaceInvitation> getInvitations(Long workspaceId) {
        findWorkspace(workspaceId);
        List<WorkspaceInvitation> invitations = invitationRepository.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
        invitations.forEach(this::markExpiredIfNecessary);
        return invitationRepository.saveAll(invitations);
    }

    @Transactional
    public WorkspaceMember acceptInvitation(String token, AcceptInvitationRequest request) {
        WorkspaceInvitation invitation = invitationRepository.findByToken(required(token, "Invitation token"))
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "token", token));
        markExpiredIfNecessary(invitation);
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation is " + invitation.getStatus().name().toLowerCase(Locale.ROOT));
        }
        String username = required(request.getUsername(), "Username");
        if (memberRepository.existsByWorkspaceIdAndUsername(invitation.getWorkspaceId(), username)) {
            throw new IllegalArgumentException("User is already a workspace member");
        }
        WorkspaceMember member = memberRepository.save(
                new WorkspaceMember(invitation.getWorkspaceId(), username, invitation.getRole()));
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());
        invitation.setAcceptedBy(username);
        invitationRepository.save(invitation);
        return member;
    }

    @Transactional
    public WorkspaceInvitation revokeInvitation(Long workspaceId, Long invitationId) {
        findWorkspace(workspaceId);
        WorkspaceInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation", "id", invitationId));
        if (!workspaceId.equals(invitation.getWorkspaceId())) {
            throw new ResourceNotFoundException("Invitation", "workspaceId", workspaceId);
        }
        markExpiredIfNecessary(invitation);
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Only pending invitations can be revoked");
        }
        invitation.setStatus(InvitationStatus.REVOKED);
        return invitationRepository.save(invitation);
    }

    private Workspace findWorkspace(Long id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace", "id", id));
    }

    private WorkspaceMember findMember(Long workspaceId, String username) {
        return memberRepository.findByWorkspaceIdAndUsername(workspaceId, username.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace member", "username", username));
    }

    private WorkspaceRole parseAssignableRole(String value, WorkspaceRole defaultRole) {
        if ((value == null || value.trim().isEmpty()) && defaultRole != null) return defaultRole;
        try {
            WorkspaceRole role = WorkspaceRole.valueOf(required(value, "Role").toUpperCase(Locale.ROOT));
            if (role == WorkspaceRole.OWNER) {
                throw new IllegalArgumentException("OWNER can only be assigned by transferring ownership");
            }
            return role;
        } catch (IllegalArgumentException exception) {
            if (exception.getMessage() != null && exception.getMessage().startsWith("OWNER")) throw exception;
            throw new IllegalArgumentException("Invalid role. Allowed roles: ADMIN, MEMBER, VIEWER");
        }
    }

    private void markExpiredIfNecessary(WorkspaceInvitation invitation) {
        if (invitation.getStatus() == InvitationStatus.PENDING
                && invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
        }
    }

    private String normalizeSlug(String value) {
        String slug = required(value, "Workspace slug").toLowerCase(Locale.ROOT);
        if (!SLUG_PATTERN.matcher(slug).matches()) {
            throw new IllegalArgumentException("Workspace slug must contain lowercase letters, numbers and hyphens only");
        }
        return slug;
    }

    private String normalizeEmail(String value) {
        String email = required(value, "Email").toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email address");
        }
        return email;
    }

    private String required(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }
}
