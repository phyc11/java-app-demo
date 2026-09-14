package com.example.billing.service;

import com.example.billing.dto.*;
import com.example.billing.model.*;
import com.example.billing.repository.*;
import com.example.billing.stripe.StripeGateway;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Locale;
import java.util.Map;

@Service
public class BillingService {

    private final SubscriptionPlanRepository planRepository;
    private final WorkspaceSubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionHistoryRepository historyRepository;
    private final WorkspaceUsageRepository usageRepository;
    private final StripeGateway stripeGateway;

    public BillingService(SubscriptionPlanRepository planRepository,
                          WorkspaceSubscriptionRepository subscriptionRepository,
                          InvoiceRepository invoiceRepository,
                          SubscriptionHistoryRepository historyRepository,
                          WorkspaceUsageRepository usageRepository,
                          StripeGateway stripeGateway) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceRepository = invoiceRepository;
        this.historyRepository = historyRepository;
        this.usageRepository = usageRepository;
        this.stripeGateway = stripeGateway;
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
        String idempotencyKey = requireIdempotencyKey(request.getIdempotencyKey());
        Invoice existing = invoiceRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
        if (existing != null) return existing;

        // If plan is FREE, activate immediately without invoice payment
        if ("FREE".equals(planName) || plan.getMonthlyPrice() == 0.0) {
            WorkspaceSubscription sub = getWorkspaceSubscription(request.getWorkspaceId());
            String previousPlan = sub.getPlanName();
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
            freeInvoice.setIdempotencyKey(idempotencyKey);
            freeInvoice.setPaidAt(LocalDateTime.now());
            Invoice saved = invoiceRepository.save(freeInvoice);
            historyRepository.save(new SubscriptionHistory(request.getWorkspaceId(), previousPlan, "FREE", "ACTIVATED", saved.getInvoiceNumber()));
            return saved;
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
        invoice.setIdempotencyKey(idempotencyKey);

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public StripePaymentResponse createStripePayment(SubscribeRequestDto request) {
        request.setPaymentGateway("STRIPE");
        Invoice invoice = subscribeOrUpgrade(request);
        if (invoice.getAmount() == 0.0) {
            return new StripePaymentResponse(invoice.getInvoiceNumber(), null, null, "succeeded");
        }
        if (invoice.getStripePaymentIntentId() != null) {
            return new StripePaymentResponse(invoice.getInvoiceNumber(), invoice.getStripePaymentIntentId(),
                    invoice.getStripeClientSecret(), invoice.getPaymentStatus());
        }
        try {
            long amountInMinorUnit = Math.round(invoice.getAmount() * 100);
            PaymentIntent intent = stripeGateway.createPaymentIntent(amountInMinorUnit, invoice.getCurrency(),
                    Map.of("invoiceNumber", invoice.getInvoiceNumber(), "workspaceId", invoice.getWorkspaceId().toString()),
                    invoice.getIdempotencyKey());
            invoice.setStripePaymentIntentId(intent.getId());
            invoice.setStripeClientSecret(intent.getClientSecret());
            invoiceRepository.save(invoice);
            return new StripePaymentResponse(invoice.getInvoiceNumber(), intent.getId(), intent.getClientSecret(), intent.getStatus());
        } catch (Exception exception) {
            throw new IllegalStateException("Stripe PaymentIntent creation failed: " + exception.getMessage(), exception);
        }
    }

    @Transactional
    public void processStripeWebhook(String payload, String signature) {
        final Event event;
        try { event = stripeGateway.verifyWebhook(payload, signature); }
        catch (Exception exception) { throw new IllegalArgumentException("Invalid Stripe webhook signature", exception); }
        if (!"payment_intent.succeeded".equals(event.getType()) && !"payment_intent.payment_failed".equals(event.getType())) return;
        Object object = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(object instanceof PaymentIntent)) throw new IllegalArgumentException("Stripe webhook has no PaymentIntent payload");
        PaymentIntent intent = (PaymentIntent) object;
        Invoice invoice = invoiceRepository.findByStripePaymentIntentId(intent.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found for PaymentIntent: " + intent.getId()));
        if ("payment_intent.succeeded".equals(event.getType())) activatePaidInvoice(invoice, intent.getId());
        else if (!"PAID".equals(invoice.getPaymentStatus())) { invoice.setPaymentStatus("FAILED"); invoiceRepository.save(invoice); }
    }

    @Transactional
    public WorkspaceUsage updateUsage(UsageUpdateRequest request) {
        if (request.getWorkspaceId()==null || request.getResourceType()==null || request.getDelta()==null)
            throw new IllegalArgumentException("workspaceId, resourceType and delta are required");
        String type=request.getResourceType().trim().toUpperCase(Locale.ROOT);
        if (!List.of("PROJECTS","MEMBERS","STORAGE").contains(type)) throw new IllegalArgumentException("Invalid resource type");
        WorkspaceUsage usage=usageRepository.findByWorkspaceIdAndResourceType(request.getWorkspaceId(),type)
                .orElse(new WorkspaceUsage(request.getWorkspaceId(),type));
        long next=usage.getUsageValue()+request.getDelta();
        if(next<0) throw new IllegalArgumentException("Usage cannot be negative");
        usage.setUsageValue(next); return usageRepository.save(usage);
    }

    public List<WorkspaceUsage> getWorkspaceUsage(Long workspaceId){return usageRepository.findByWorkspaceId(workspaceId);}
    public List<SubscriptionHistory> getSubscriptionHistory(Long workspaceId){return historyRepository.findByWorkspaceIdOrderByChangedAtDesc(workspaceId);}

    private void activatePaidInvoice(Invoice invoice,String transactionId){
        if("PAID".equals(invoice.getPaymentStatus())) return;
        WorkspaceSubscription sub=getWorkspaceSubscription(invoice.getWorkspaceId());
        String previous=sub.getPlanName();
        invoice.setPaymentStatus("PAID"); invoice.setTransactionId(transactionId); invoice.setPaidAt(LocalDateTime.now()); invoiceRepository.save(invoice);
        sub.setPlanName(invoice.getPlanName()); sub.setOwnerUsername(invoice.getOwnerUsername()); sub.setStatus("ACTIVE");
        sub.setStartDate(LocalDate.now()); sub.setEndDate(LocalDate.now().plusMonths(1)); subscriptionRepository.save(sub);
        historyRepository.save(new SubscriptionHistory(invoice.getWorkspaceId(),previous,invoice.getPlanName(),"ACTIVATED",invoice.getInvoiceNumber()));
    }

    private String requireIdempotencyKey(String value){
        if(value==null || value.trim().length()<8 || value.trim().length()>100) throw new IllegalArgumentException("Idempotency key must be 8-100 characters");
        return value.trim();
    }

    public QuotaStatusDto checkQuota(UsageQuotaCheckDto checkDto) {
        if (checkDto == null || checkDto.getWorkspaceId() == null) throw new IllegalArgumentException("Workspace ID is required");
        if (checkDto.getCurrentProjectCount() == null)
            checkDto.setCurrentProjectCount(usageValue(checkDto.getWorkspaceId(), "PROJECTS").intValue());
        if (checkDto.getCurrentMemberCount() == null)
            checkDto.setCurrentMemberCount(usageValue(checkDto.getWorkspaceId(), "MEMBERS").intValue());
        if (checkDto.getCurrentStorageMb() == null)
            checkDto.setCurrentStorageMb(usageValue(checkDto.getWorkspaceId(), "STORAGE"));
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

    private Long usageValue(Long workspaceId, String type) {
        return usageRepository.findByWorkspaceIdAndResourceType(workspaceId, type)
                .map(WorkspaceUsage::getUsageValue).orElse(0L);
    }
}
