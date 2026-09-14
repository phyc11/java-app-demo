package com.example.workspace.service;

import com.example.workspace.dto.*;
import com.example.workspace.model.*;
import com.example.workspace.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WorkspaceServiceIntegrationTest {
    @Autowired private WorkspaceService workspaceService;
    @Autowired private WorkspaceRepository workspaceRepository;
    @Autowired private WorkspaceMemberRepository memberRepository;
    @Autowired private WorkspaceInvitationRepository invitationRepository;

    @BeforeEach
    void cleanDatabase() {
        invitationRepository.deleteAll();
        memberRepository.deleteAll();
        workspaceRepository.deleteAll();
    }

    @Test
    void createWorkspaceAlsoCreatesOwnerMembership() {
        Workspace workspace = createWorkspace("acme", "alice");

        WorkspaceMember owner = memberRepository
                .findByWorkspaceIdAndUsername(workspace.getId(), "alice")
                .orElseThrow();
        assertEquals(WorkspaceRole.OWNER, owner.getRole());
        assertEquals("ACTIVE", workspace.getStatus());
    }

    @Test
    void inviteAndAcceptAddsMemberWithRequestedRole() {
        Workspace workspace = createWorkspace("acme", "alice");
        CreateInvitationRequest invite = new CreateInvitationRequest();
        invite.setEmail("bob@example.com");
        invite.setInvitedBy("alice");
        invite.setRole("viewer");

        WorkspaceInvitation invitation = workspaceService.createInvitation(workspace.getId(), invite);
        AcceptInvitationRequest accept = new AcceptInvitationRequest();
        accept.setUsername("bob");
        WorkspaceMember member = workspaceService.acceptInvitation(invitation.getToken(), accept);

        assertEquals(WorkspaceRole.VIEWER, member.getRole());
        WorkspaceInvitation accepted = invitationRepository.findById(invitation.getId()).orElseThrow();
        assertEquals(InvitationStatus.ACCEPTED, accepted.getStatus());
        assertEquals("bob", accepted.getAcceptedBy());
    }

    @Test
    void ownerCannotBeRemovedOrChangedThroughRoleEndpoint() {
        Workspace workspace = createWorkspace("acme", "alice");
        RoleRequest role = new RoleRequest();
        role.setRole("member");

        assertThrows(IllegalArgumentException.class,
                () -> workspaceService.removeMember(workspace.getId(), "alice"));
        assertThrows(IllegalArgumentException.class,
                () -> workspaceService.updateMemberRole(workspace.getId(), "alice", role));
    }

    @Test
    void transferOwnershipDemotesPreviousOwnerToAdmin() {
        Workspace workspace = createWorkspace("acme", "alice");
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setUsername("bob");
        memberRequest.setRole("member");
        workspaceService.addMember(workspace.getId(), memberRequest);

        Workspace updated = workspaceService.transferOwnership(workspace.getId(), "bob");

        assertEquals("bob", updated.getOwnerUsername());
        assertEquals(WorkspaceRole.ADMIN, memberRepository
                .findByWorkspaceIdAndUsername(workspace.getId(), "alice").orElseThrow().getRole());
        assertEquals(WorkspaceRole.OWNER, memberRepository
                .findByWorkspaceIdAndUsername(workspace.getId(), "bob").orElseThrow().getRole());
    }

    @Test
    void rejectsDuplicateSlugAndDuplicatePendingInvitation() {
        Workspace workspace = createWorkspace("acme", "alice");
        assertThrows(IllegalArgumentException.class, () -> createWorkspace("acme", "other"));

        CreateInvitationRequest invite = new CreateInvitationRequest();
        invite.setEmail("BOB@example.com");
        invite.setInvitedBy("alice");
        workspaceService.createInvitation(workspace.getId(), invite);

        assertThrows(IllegalArgumentException.class,
                () -> workspaceService.createInvitation(workspace.getId(), invite));
    }

    private Workspace createWorkspace(String slug, String owner) {
        CreateWorkspaceRequest request = new CreateWorkspaceRequest();
        request.setName("Acme Workspace");
        request.setSlug(slug);
        request.setOwnerUsername(owner);
        return workspaceService.createWorkspace(request);
    }
}
