package com.example.billing.controller;

import com.example.billing.dto.*;
import com.example.billing.model.*;
import com.example.billing.service.BillingService;
import com.example.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/payment/process")
    public ResponseEntity<ApiResponse<PaymentProcessDto>> processPayment(@RequestBody PaymentProcessDto paymentDto) {
        PaymentProcessDto result = billingService.processPaymentCallback(paymentDto);
        return ResponseEntity.ok(ApiResponse.ok("Payment status processed", result));
    }

    @PostMapping("/quota/check")
    public ResponseEntity<ApiResponse<QuotaStatusDto>> checkQuota(@RequestBody UsageQuotaCheckDto checkDto) {
        QuotaStatusDto status = billingService.checkQuota(checkDto);
        return ResponseEntity.ok(ApiResponse.ok("Quota check complete", status));
    }

    @GetMapping("/invoices/workspace/{workspaceId}")
    public ResponseEntity<ApiResponse<List<Invoice>>> getInvoicesByWorkspace(@PathVariable Long workspaceId) {
        List<Invoice> invoices = billingService.getInvoicesByWorkspace(workspaceId);
        return ResponseEntity.ok(ApiResponse.ok("Invoices retrieved for workspace", invoices));
    }

    @GetMapping("/invoices/user/{username}")
    public ResponseEntity<ApiResponse<List<Invoice>>> getInvoicesByUser(@PathVariable String username) {
        List<Invoice> invoices = billingService.getInvoicesByUser(username);
        return ResponseEntity.ok(ApiResponse.ok("Invoices retrieved for user", invoices));
    }
}
