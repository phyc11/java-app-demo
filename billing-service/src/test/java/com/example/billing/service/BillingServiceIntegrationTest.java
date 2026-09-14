package com.example.billing.service;

import com.example.billing.dto.*;
import com.example.billing.model.*;
import com.example.billing.repository.*;
import com.example.billing.stripe.StripeGateway;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BillingServiceIntegrationTest {
    @Autowired private BillingService billingService;
    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private WorkspaceSubscriptionRepository subscriptionRepository;
    @Autowired private SubscriptionHistoryRepository historyRepository;
    @Autowired private WorkspaceUsageRepository usageRepository;
    @MockBean private StripeGateway stripeGateway;

    @BeforeEach
    void cleanTransactions() {
        historyRepository.deleteAll();
        invoiceRepository.deleteAll();
        usageRepository.deleteAll();
        subscriptionRepository.deleteAll();
        reset(stripeGateway);
    }

    @Test
    void createsStripePaymentIntentInTestGateway() throws Exception {
        PaymentIntent intent = new PaymentIntent();
        intent.setId("pi_test_123");
        intent.setClientSecret("pi_test_123_secret");
        intent.setStatus("requires_payment_method");
        when(stripeGateway.createPaymentIntent(eq(1900L), eq("USD"), anyMap(), eq("checkout-123")))
                .thenReturn(intent);

        StripePaymentResponse response = billingService.createStripePayment(request("PRO", "checkout-123"));

        assertEquals("pi_test_123", response.getPaymentIntentId());
        Invoice invoice = invoiceRepository.findByInvoiceNumber(response.getInvoiceNumber()).orElseThrow();
        assertEquals("PENDING", invoice.getPaymentStatus());
    }

    @Test
    void repeatedIdempotencyKeyDoesNotCreateSecondPaymentIntent() throws Exception {
        PaymentIntent intent = new PaymentIntent();
        intent.setId("pi_once"); intent.setClientSecret("secret"); intent.setStatus("requires_payment_method");
        when(stripeGateway.createPaymentIntent(anyLong(), anyString(), anyMap(), eq("same-key-123"))).thenReturn(intent);

        StripePaymentResponse first = billingService.createStripePayment(request("PRO", "same-key-123"));
        StripePaymentResponse second = billingService.createStripePayment(request("PRO", "same-key-123"));

        assertEquals(first.getInvoiceNumber(), second.getInvoiceNumber());
        assertEquals(1, invoiceRepository.count());
        verify(stripeGateway, times(1)).createPaymentIntent(anyLong(), anyString(), anyMap(), anyString());
    }

    @Test
    void invalidWebhookSignatureCannotChangeBillingState() throws Exception {
        when(stripeGateway.verifyWebhook(anyString(), anyString())).thenThrow(new SecurityException("bad signature"));
        assertThrows(IllegalArgumentException.class,
                () -> billingService.processStripeWebhook("{}", "invalid"));
        assertEquals(0, invoiceRepository.count());
    }

    @Test
    void tracksQuotaByWorkspaceAndRejectsLimit() {
        UsageUpdateRequest update = new UsageUpdateRequest();
        update.setWorkspaceId(10L); update.setResourceType("PROJECTS"); update.setDelta(3L);
        billingService.updateUsage(update);

        UsageQuotaCheckDto check = new UsageQuotaCheckDto();
        check.setWorkspaceId(10L);
        QuotaStatusDto result = billingService.checkQuota(check);

        assertFalse(result.isAllowed());
        assertEquals("PROJECTS", result.getResourceType());
        assertEquals(3L, result.getCurrentUsage());
    }

    @Test
    void freeSubscriptionCreatesInvoiceAndHistoryOnce() {
        Invoice first = billingService.subscribeOrUpgrade(request("FREE", "free-plan-123"));
        Invoice second = billingService.subscribeOrUpgrade(request("FREE", "free-plan-123"));

        assertEquals(first.getId(), second.getId());
        assertEquals("PAID", first.getPaymentStatus());
        assertEquals(1, invoiceRepository.count());
        assertEquals(1, historyRepository.findByWorkspaceIdOrderByChangedAtDesc(10L).size());
    }

    @Test
    void usageCannotBecomeNegative() {
        UsageUpdateRequest update = new UsageUpdateRequest();
        update.setWorkspaceId(10L); update.setResourceType("STORAGE"); update.setDelta(-1L);
        assertThrows(IllegalArgumentException.class, () -> billingService.updateUsage(update));
    }

    private SubscribeRequestDto request(String plan, String key) {
        SubscribeRequestDto request = new SubscribeRequestDto();
        request.setWorkspaceId(10L);
        request.setOwnerUsername("alice");
        request.setPlanName(plan);
        request.setCurrency("USD");
        request.setIdempotencyKey(key);
        return request;
    }
}
