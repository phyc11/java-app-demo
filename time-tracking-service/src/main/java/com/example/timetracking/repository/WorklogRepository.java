package com.example.timetracking.repository;

import com.example.timetracking.model.Worklog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorklogRepository extends JpaRepository<Worklog, Long> {

    List<Worklog> findByTaskId(Long taskId);

    List<Worklog> findByUsername(String username);

    List<Worklog> findByProjectId(Long projectId);

    @Query("SELECT SUM(w.timeSpentHours) FROM Worklog w WHERE w.taskId = :taskId")
    Double sumTimeSpentByTaskId(@Param("taskId") Long taskId);

    @Query("SELECT SUM(w.timeSpentHours) FROM Worklog w WHERE w.username = :username")
    Double sumTimeSpentByUsername(@Param("username") String username);

    @Query("SELECT COUNT(DISTINCT w.taskId) FROM Worklog w WHERE w.username = :username")
    Long countDistinctTasksByUsername(@Param("username") String username);

    @Query("SELECT COUNT(w) FROM Worklog w WHERE w.username = :username")
    Long countLogsByUsername(@Param("username") String username);

    @Query("SELECT SUM(w.timeSpentHours) FROM Worklog w WHERE w.projectId = :projectId")
    Double sumTimeSpentByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(DISTINCT w.taskId) FROM Worklog w WHERE w.projectId = :projectId")
    Long countDistinctTasksByProjectId(@Param("projectId") Long projectId);
}
