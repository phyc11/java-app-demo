package com.example.billing.controller;

import com.example.billing.dto.*;
import com.example.billing.model.*;
import com.example.billing.service.BillingService;
import com.example.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<SubscriptionPlan>>> getAllPlans() {
        List<SubscriptionPlan> plans = billingService.getAllPlans();
        return ResponseEntity.ok(ApiResponse.ok("Subscription plans retrieved", plans));
    }

    @GetMapping("/subscription/workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceSubscription>> getSubscription(@PathVariable Long workspaceId) {
        WorkspaceSubscription sub = billingService.getWorkspaceSubscription(workspaceId);
        return ResponseEntity.ok(ApiResponse.ok("Workspace subscription retrieved", sub));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Invoice>> subscribeOrUpgrade(@RequestBody SubscribeRequestDto request) {
        Invoice invoice = billingService.subscribeOrUpgrade(request);
        return ResponseEntity.ok(ApiResponse.ok("Subscription request created. Invoice generated.", invoice));
    }

    @PostMapping("/stripe/payment-intents")
    public ResponseEntity<ApiResponse<StripePaymentResponse>> createStripePayment(@RequestBody SubscribeRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok("Stripe PaymentIntent created", billingService.createStripePayment(request)));
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<Void> stripeWebhook(@RequestBody String payload,
                                               @RequestHeader("Stripe-Signature") String signature) {
        billingService.processStripeWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/quota/check")
    public ResponseEntity<ApiResponse<QuotaStatusDto>> checkQuota(@RequestBody UsageQuotaCheckDto checkDto) {
        QuotaStatusDto status = billingService.checkQuota(checkDto);
        return ResponseEntity.ok(ApiResponse.ok("Quota check complete", status));
    }

    @GetMapping("/invoices/workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<List<Invoice>>> getInvoicesByWorkspace(@PathVariable Long workspaceId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<Invoice> invoices = billingService.getInvoicesByWorkspace(workspaceId, page, size);
        return ResponseEntity.ok(ApiResponse.okPage("Invoices retrieved for workspace", invoices.getContent(),
                invoices.getNumber(), invoices.getSize(), invoices.getTotalElements(), invoices.getTotalPages()));
    }

    @GetMapping("/invoices/user/{username}")
    public ResponseEntity<ApiResponse<List<Invoice>>> getInvoicesByUser(@PathVariable String username,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<Invoice> invoices = billingService.getInvoicesByUser(username, page, size);
        return ResponseEntity.ok(ApiResponse.okPage("Invoices retrieved for user", invoices.getContent(),
                invoices.getNumber(), invoices.getSize(), invoices.getTotalElements(), invoices.getTotalPages()));
    }

    @PostMapping("/usage")
    public ResponseEntity<ApiResponse<WorkspaceUsage>> updateUsage(@RequestBody UsageUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Workspace usage updated", billingService.updateUsage(request)));
    }

    @GetMapping("/usage/workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<List<WorkspaceUsage>>> getUsage(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(ApiResponse.ok("Workspace usage retrieved", billingService.getWorkspaceUsage(workspaceId)));
    }

    @GetMapping("/subscriptions/workspace/{workspaceId}/history")
    public ResponseEntity<ApiResponse<List<SubscriptionHistory>>> getSubscriptionHistory(@PathVariable Long workspaceId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<SubscriptionHistory> history = billingService.getSubscriptionHistory(workspaceId, page, size);
        return ResponseEntity.ok(ApiResponse.okPage("Subscription history retrieved", history.getContent(),
                history.getNumber(), history.getSize(), history.getTotalElements(), history.getTotalPages()));
    }
}
