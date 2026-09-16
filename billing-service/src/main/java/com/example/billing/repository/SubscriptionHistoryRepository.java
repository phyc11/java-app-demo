package com.example.billing.repository;
import com.example.billing.model.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory,Long>{
    List<SubscriptionHistory> findByWorkspaceIdOrderByChangedAtDesc(Long workspaceId);
    Page<SubscriptionHistory> findByWorkspaceId(Long workspaceId, Pageable pageable);
}
