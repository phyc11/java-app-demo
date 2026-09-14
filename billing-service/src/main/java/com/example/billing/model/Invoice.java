package com.example.billing.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false)
    private String ownerUsername;

    private String planName;
    private Double amount;
    private String currency; // USD, VND

    private String paymentGateway; // STRIPE, VNPAY, MOMO
    private String paymentStatus; // PENDING, PAID, FAILED

    private String transactionId;
    @Column(unique = true)
    private String idempotencyKey;
    @Column(unique = true)
    private String stripePaymentIntentId;
    @Column(length = 255)
    private String stripeClientSecret;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Invoice() {
        this.createdAt = LocalDateTime.now();
        this.paymentStatus = "PENDING";
        this.currency = "USD";
    }

    public Invoice(String invoiceNumber, Long workspaceId, String ownerUsername, String planName, Double amount, String currency, String paymentGateway) {
        this.invoiceNumber = invoiceNumber;
        this.workspaceId = workspaceId;
        this.ownerUsername = ownerUsername;
        this.planName = planName;
        this.amount = amount;
        this.currency = currency != null ? currency : "USD";
        this.paymentGateway = paymentGateway != null ? paymentGateway.toUpperCase() : "STRIPE";
        this.paymentStatus = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentGateway() { return paymentGateway; }
    public void setPaymentGateway(String paymentGateway) { this.paymentGateway = paymentGateway; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public void setStripePaymentIntentId(String stripePaymentIntentId) { this.stripePaymentIntentId = stripePaymentIntentId; }
    public String getStripeClientSecret() { return stripeClientSecret; }
    public void setStripeClientSecret(String stripeClientSecret) { this.stripeClientSecret = stripeClientSecret; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
