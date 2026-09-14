package com.example.billing.repository;
import com.example.billing.model.WorkspaceUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional;
public interface WorkspaceUsageRepository extends JpaRepository<WorkspaceUsage,Long>{
    Optional<WorkspaceUsage> findByWorkspaceIdAndResourceType(Long workspaceId,String resourceType);
    List<WorkspaceUsage> findByWorkspaceId(Long workspaceId);
}
