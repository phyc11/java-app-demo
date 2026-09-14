package com.example.workspace.dto;

import com.example.workspace.model.Workspace;
import com.example.workspace.model.WorkspaceMember;

import java.util.List;

public class WorkspaceDetailDto {
    private Workspace workspace;
    private List<WorkspaceMember> members;

    public WorkspaceDetailDto(Workspace workspace, List<WorkspaceMember> members) {
        this.workspace = workspace;
        this.members = members;
    }

    public Workspace getWorkspace() { return workspace; }
    public void setWorkspace(Workspace workspace) { this.workspace = workspace; }
    public List<WorkspaceMember> getMembers() { return members; }
    public void setMembers(List<WorkspaceMember> members) { this.members = members; }
}
