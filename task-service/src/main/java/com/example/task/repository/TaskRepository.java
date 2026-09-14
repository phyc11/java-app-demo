package com.example.task.repository;

import com.example.task.model.Priority;
import com.example.task.model.Status;
import com.example.task.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCreatedBy(String createdBy);

    @Query("SELECT t FROM Task t WHERE " +
           "(:workspaceId IS NULL OR t.workspaceId = :workspaceId) AND " +
           "(:projectId IS NULL OR t.projectId = :projectId) AND " +
           "(:assignee IS NULL OR t.assignee = :assignee) AND " +
           "(:parentTaskId IS NULL OR t.parentTaskId = :parentTaskId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
           "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> findFilteredTasks(@Param("workspaceId") Long workspaceId,
                                @Param("projectId") Long projectId,
                                @Param("assignee") String assignee,
                                @Param("parentTaskId") Long parentTaskId,
                                @Param("status") Status status,
                                @Param("priority") Priority priority,
                                @Param("categoryId") Long categoryId,
                                @Param("search") String search, Pageable pageable);

    long countByStatus(Status status);
    long countByWorkspaceIdAndStatus(Long workspaceId, Status status);
    long countByWorkspaceId(Long workspaceId);
    List<Task> findByParentTaskId(Long parentTaskId);
}
