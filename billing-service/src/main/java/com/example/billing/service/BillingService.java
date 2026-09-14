package com.example.billing.service;

import com.example.billing.dto.*;
import com.example.billing.model.*;
import com.example.billing.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BillingService {

    private final SubscriptionPlanRepository planRepository;
    private final WorkspaceSubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;

    public BillingService(SubscriptionPlanRepository planRepository,
                          WorkspaceSubscriptionRepository subscriptionRepository,
                          InvoiceRepository invoiceRepository) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @PostConstruct
    public void initDefaultPlans() {
        if (planRepository.count() == 0) {
            planRepository.save(new SubscriptionPlan(
                    "FREE",
                    0.0,
                    3,
                    5,
                    500L,
                    "Free plan for small personal projects and startup teams."
            ));
            planRepository.save(new SubscriptionPlan(
                    "PRO",
                    19.0,
                    50,
                    30,
                    10240L,
                    "Professional plan for growing teams with advanced collaboration."
            ));
            planRepository.save(new SubscriptionPlan(
                    "ENTERPRISE",
                    99.0,
                    -1,
                    -1,
                    -1L,
                    "Enterprise tier with unlimited projects, members, storage, and dedicated support."
            ));
        }
    }

    public List<SubscriptionPlan> getAllPlans() {
        return planRepository.findAll();
    }

    public WorkspaceSubscription getWorkspaceSubscription(Long workspaceId) {
        return subscriptionRepository.findByWorkspaceId(workspaceId)
                .orElseGet(() -> {
                    WorkspaceSubscription sub = new WorkspaceSubscription(
                            workspaceId,
                            "system",
                            "FREE",
                            LocalDate.now(),
                            LocalDate.now().plusYears(100)
                    );
                    return subscriptionRepository.save(sub);
                });
    }

    @Transactional
    public Invoice subscribeOrUpgrade(SubscribeRequestDto request) {
        if (request.getWorkspaceId() == null) {
            throw new IllegalArgumentException("Workspace ID is required!");
        }
        if (request.getOwnerUsername() == null || request.getOwnerUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Owner username is required!");
        }

        String planName = request.getPlanName() != null ? request.getPlanName().toUpperCase() : "FREE";
        SubscriptionPlan plan = planRepository.findByPlanName(planName)
                .orElseThrow(() -> new IllegalArgumentException("Subscription plan not found: " + planName));

        // If plan is FREE, activate immediately without invoice payment
        if ("FREE".equals(planName) || plan.getMonthlyPrice() == 0.0) {
            WorkspaceSubscription sub = getWorkspaceSubscription(request.getWorkspaceId());
            sub.setPlanName("FREE");
            sub.setOwnerUsername(request.getOwnerUsername());
            sub.setStatus("ACTIVE");
            subscriptionRepository.save(sub);

            Invoice freeInvoice = new Invoice(
                    "INV-FREE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    request.getWorkspaceId(),
                    request.getOwnerUsername(),
                    "FREE",
                    0.0,
                    request.getCurrency() != null ? request.getCurrency() : "USD",
                    "SYSTEM"
            );
            freeInvoice.setPaymentStatus("PAID");
            freeInvoice.setPaidAt(LocalDateTime.now());
            return invoiceRepository.save(freeInvoice);
        }

        // For PRO / ENTERPRISE, generate Invoice for Payment Gateway processing
        String gateway = request.getPaymentGateway() != null ? request.getPaymentGateway().toUpperCase() : "STRIPE";
        List<String> validGateways = List.of("STRIPE", "VNPAY", "MOMO");
        if (!validGateways.contains(gateway)) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + gateway + ". Allowed: " + validGateways);
        }

        String invoiceNum = "INV-" + LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Invoice invoice = new Invoice(
                invoiceNum,
                request.getWorkspaceId(),
                request.getOwnerUsername(),
                planName,
                plan.getMonthlyPrice(),
                request.getCurrency() != null ? request.getCurrency() : "USD",
                gateway
        );

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public PaymentProcessDto processPaymentCallback(PaymentProcessDto paymentDto) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(paymentDto.getInvoiceNumber())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + paymentDto.getInvoiceNumber()));

        if ("PAID".equals(invoice.getPaymentStatus())) {
            paymentDto.setSuccess(true);
            paymentDto.setMessage("Invoice has already been paid successfully.");
            return paymentDto;
        }

        if (paymentDto.isSuccess()) {
            invoice.setPaymentStatus("PAID");
            invoice.setTransactionId(paymentDto.getTransactionId() != null ? paymentDto.getTransactionId() : "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            invoice.setPaidAt(LocalDateTime.now());
            invoiceRepository.save(invoice);

            // Update workspace active subscription
            WorkspaceSubscription sub = getWorkspaceSubscription(invoice.getWorkspaceId());
            sub.setPlanName(invoice.getPlanName());
            sub.setOwnerUsername(invoice.getOwnerUsername());
            sub.setStatus("ACTIVE");
            sub.setStartDate(LocalDate.now());
            sub.setEndDate(LocalDate.now().plusMonths(1));
            subscriptionRepository.save(sub);

            paymentDto.setMessage("Payment processed via " + invoice.getPaymentGateway() + ". Subscription upgraded to " + invoice.getPlanName() + "!");
        } else {
            invoice.setPaymentStatus("FAILED");
            invoiceRepository.save(invoice);
            paymentDto.setMessage("Payment failed via " + invoice.getPaymentGateway() + ". Invoice status marked as FAILED.");
        }

        return paymentDto;
    }

    public QuotaStatusDto checkQuota(UsageQuotaCheckDto checkDto) {
        WorkspaceSubscription sub = getWorkspaceSubscription(checkDto.getWorkspaceId());
        SubscriptionPlan plan = planRepository.findByPlanName(sub.getPlanName())
                .orElseGet(() -> planRepository.findByPlanName("FREE").get());

        // Check Projects Quota
        if (checkDto.getCurrentProjectCount() != null && plan.getMaxProjects() != -1) {
            if (checkDto.getCurrentProjectCount() >= plan.getMaxProjects()) {
                return new QuotaStatusDto(
                        plan.getPlanName(),
                        false,
                        "Project quota limit reached for " + plan.getPlanName() + " plan (" + plan.getMaxProjects() + " max). Upgrade your plan to create more projects.",
                        "PROJECTS",
                        checkDto.getCurrentProjectCount().longValue(),
                        plan.getMaxProjects().longValue()
                );
            }
        }

        // Check Members Quota
        if (checkDto.getCurrentMemberCount() != null && plan.getMaxMembersPerProject() != -1) {
            if (checkDto.getCurrentMemberCount() >= plan.getMaxMembersPerProject()) {
                return new QuotaStatusDto(
                        plan.getPlanName(),
                        false,
                        "Member quota limit reached for " + plan.getPlanName() + " plan (" + plan.getMaxMembersPerProject() + " max). Upgrade your plan to add more members.",
                        "MEMBERS",
                        checkDto.getCurrentMemberCount().longValue(),
                        plan.getMaxMembersPerProject().longValue()
                );
            }
        }

        // Check Storage Quota
        if (checkDto.getCurrentStorageMb() != null && plan.getMaxStorageMb() != -1) {
            if (checkDto.getCurrentStorageMb() >= plan.getMaxStorageMb()) {
                return new QuotaStatusDto(
                        plan.getPlanName(),
                        false,
                        "Storage quota limit reached for " + plan.getPlanName() + " plan (" + plan.getMaxStorageMb() + " MB max). Upgrade your plan for more storage.",
                        "STORAGE",
                        checkDto.getCurrentStorageMb(),
                        plan.getMaxStorageMb()
                );
            }
        }

        return new QuotaStatusDto(
                plan.getPlanName(),
                true,
                "Action allowed within " + plan.getPlanName() + " quota limits.",
                "OK",
                0L,
                0L
        );
    }

    public List<Invoice> getInvoicesByWorkspace(Long workspaceId) {
        return invoiceRepository.findByWorkspaceId(workspaceId);
    }

    public List<Invoice> getInvoicesByUser(String username) {
        return invoiceRepository.findByOwnerUsername(username);
    }
}
