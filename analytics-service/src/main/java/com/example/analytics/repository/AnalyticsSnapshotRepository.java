package com.example.analytics.repository;
import com.example.analytics.model.AnalyticsSnapshot; import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime; import java.util.Optional;
public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot,Long>{
 Optional<AnalyticsSnapshot> findFirstByWorkspaceIdAndProjectIdOrderByCapturedAtDesc(Long workspaceId,Long projectId);
 Optional<AnalyticsSnapshot> findFirstByWorkspaceIdAndProjectIdIsNullOrderByCapturedAtDesc(Long workspaceId);
 Page<AnalyticsSnapshot> findByWorkspaceIdAndProjectId(Long workspaceId,Long projectId,Pageable pageable);
 Page<AnalyticsSnapshot> findByWorkspaceIdAndProjectIdIsNull(Long workspaceId,Pageable pageable);
 long deleteByCapturedAtBefore(LocalDateTime cutoff);
}
