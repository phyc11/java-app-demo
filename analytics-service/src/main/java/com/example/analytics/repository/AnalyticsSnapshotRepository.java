package com.example.analytics.repository;
import com.example.analytics.model.AnalyticsSnapshot; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot,Long>{
 Optional<AnalyticsSnapshot> findFirstByWorkspaceIdAndProjectIdOrderByCapturedAtDesc(Long workspaceId,Long projectId);
 Optional<AnalyticsSnapshot> findFirstByWorkspaceIdAndProjectIdIsNullOrderByCapturedAtDesc(Long workspaceId);
}
