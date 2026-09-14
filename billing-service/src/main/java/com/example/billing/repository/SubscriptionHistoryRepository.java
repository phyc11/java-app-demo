package com.example.billing.repository;
import com.example.billing.model.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory,Long>{
    List<SubscriptionHistory> findByWorkspaceIdOrderByChangedAtDesc(Long workspaceId);
}
