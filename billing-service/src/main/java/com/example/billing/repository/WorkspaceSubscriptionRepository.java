package com.example.billing.repository;

import com.example.billing.model.WorkspaceSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceSubscriptionRepository extends JpaRepository<WorkspaceSubscription, Long> {
    Optional<WorkspaceSubscription> findByWorkspaceId(Long workspaceId);
}
