package com.example.timetracking.repository;

import com.example.timetracking.model.TaskEstimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskEstimateRepository extends JpaRepository<TaskEstimate, Long> {

    @Query("SELECT SUM(e.estimatedHours) FROM TaskEstimate e WHERE e.projectId = :projectId")
    Double sumEstimatedHoursByProjectId(@Param("projectId") Long projectId);
}
