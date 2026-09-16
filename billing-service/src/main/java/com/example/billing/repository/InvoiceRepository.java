package com.example.billing.repository;

import com.example.billing.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByWorkspaceId(Long workspaceId);
    List<Invoice> findByOwnerUsername(String ownerUsername);
    Page<Invoice> findByWorkspaceId(Long workspaceId, Pageable pageable);
    Page<Invoice> findByOwnerUsername(String ownerUsername, Pageable pageable);
    Optional<Invoice> findByIdempotencyKey(String idempotencyKey);
    Optional<Invoice> findByStripePaymentIntentId(String stripePaymentIntentId);
}
