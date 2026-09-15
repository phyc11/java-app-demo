package com.example.project.repository;

import com.example.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectKey(String projectKey);
    boolean existsByWorkspaceIdAndProjectKey(Long workspaceId,String projectKey);
    @Query("select p from Project p where p.workspaceId=:workspaceId and (:status is null or p.status=:status) and (:search is null or lower(p.name) like lower(concat('%',:search,'%')) or lower(p.projectKey) like lower(concat('%',:search,'%'))) and (:workspaceAdmin=true or exists (select m.id from ProjectMember m where m.projectId=p.id and lower(m.username)=lower(:username)))")
    Page<Project> searchAccessible(@Param("workspaceId")Long workspaceId,@Param("status")String status,@Param("search")String search,@Param("username")String username,@Param("workspaceAdmin")boolean workspaceAdmin,Pageable pageable);
}
