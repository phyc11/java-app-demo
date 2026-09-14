package com.example.workspace.controller;

import com.example.common.dto.ApiResponse;
import com.example.workspace.dto.*;
import com.example.workspace.model.*;
import com.example.workspace.service.WorkspaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Workspace>> create(@RequestBody CreateWorkspaceRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Workspace created", workspaceService.createWorkspace(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Workspace>>> list(
            @RequestParam(required = false) String username) {
        return ResponseEntity.ok(ApiResponse.ok("Workspaces retrieved", workspaceService.getWorkspaces(username)));
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceDetailDto>> detail(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(ApiResponse.ok("Workspace retrieved", workspaceService.getWorkspace(workspaceId)));
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<Workspace>> update(@PathVariable Long workspaceId,
                                                          @RequestBody UpdateWorkspaceRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Workspace updated", workspaceService.updateWorkspace(workspaceId, request)));
    }

    @PutMapping("/{workspaceId}/archive")
    public ResponseEntity<ApiResponse<Workspace>> archive(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(ApiResponse.ok("Workspace archived", workspaceService.archiveWorkspace(workspaceId)));
    }

    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<List<WorkspaceMember>>> members(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(ApiResponse.ok("Members retrieved", workspaceService.getMembers(workspaceId)));
    }

    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<ApiResponse<WorkspaceMember>> addMember(@PathVariable Long workspaceId,
                                                                   @RequestBody MemberRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Member added", workspaceService.addMember(workspaceId, request)));
    }

    @PutMapping("/{workspaceId}/members/{username}/role")
    public ResponseEntity<ApiResponse<WorkspaceMember>> updateRole(@PathVariable Long workspaceId,
                                                                    @PathVariable String username,
                                                                    @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Member role updated",
                workspaceService.updateMemberRole(workspaceId, username, request)));
    }

    @DeleteMapping("/{workspaceId}/members/{username}")
    public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable Long workspaceId,
                                                           @PathVariable String username) {
        workspaceService.removeMember(workspaceId, username);
        return ResponseEntity.ok(ApiResponse.ok("Member removed", null));
    }

    @PutMapping("/{workspaceId}/owner/{username}")
    public ResponseEntity<ApiResponse<Workspace>> transferOwnership(@PathVariable Long workspaceId,
                                                                     @PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.ok("Ownership transferred",
                workspaceService.transferOwnership(workspaceId, username)));
    }

    @PostMapping("/{workspaceId}/invitations")
    public ResponseEntity<ApiResponse<WorkspaceInvitation>> invite(@PathVariable Long workspaceId,
                                                                    @RequestBody CreateInvitationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Invitation created",
                workspaceService.createInvitation(workspaceId, request)));
    }

    @GetMapping("/{workspaceId}/invitations")
    public ResponseEntity<ApiResponse<List<WorkspaceInvitation>>> invitations(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(ApiResponse.ok("Invitations retrieved",
                workspaceService.getInvitations(workspaceId)));
    }

    @PostMapping("/invitations/{token}/accept")
    public ResponseEntity<ApiResponse<WorkspaceMember>> accept(@PathVariable String token,
                                                                @RequestBody AcceptInvitationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Invitation accepted",
                workspaceService.acceptInvitation(token, request)));
    }

    @PutMapping("/{workspaceId}/invitations/{invitationId}/revoke")
    public ResponseEntity<ApiResponse<WorkspaceInvitation>> revoke(@PathVariable Long workspaceId,
                                                                    @PathVariable Long invitationId) {
        return ResponseEntity.ok(ApiResponse.ok("Invitation revoked",
                workspaceService.revokeInvitation(workspaceId, invitationId)));
    }
}
