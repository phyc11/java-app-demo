package com.example.search.repository;

import com.example.search.model.TaskDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskSearchRepository extends JpaRepository<TaskDocument, Long> {

    @Query("SELECT t FROM TaskDocument t WHERE " +
           "(:query IS NULL OR :query = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.tags) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR t.status = :status) AND " +
           "(:priority IS NULL OR :priority = '' OR t.priority = :priority) AND " +
           "(:categoryName IS NULL OR :categoryName = '' OR LOWER(t.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%'))) AND " +
           "(:assigneeName IS NULL OR :assigneeName = '' OR LOWER(t.assigneeName) LIKE LOWER(CONCAT('%', :assigneeName, '%')))")
    List<TaskDocument> searchTasks(@Param("query") String query,
                                   @Param("status") String status,
                                   @Param("priority") String priority,
                                   @Param("categoryName") String categoryName,
                                   @Param("assigneeName") String assigneeName);

    @Query("SELECT DISTINCT t.title FROM TaskDocument t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<String> findSuggestions(@Param("query") String query);
}
