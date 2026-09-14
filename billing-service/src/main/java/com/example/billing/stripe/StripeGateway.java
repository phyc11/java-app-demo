package com.example.billing.stripe;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;

import java.util.Map;

public interface StripeGateway {
    PaymentIntent createPaymentIntent(long amount, String currency, Map<String,String> metadata, String idempotencyKey) throws Exception;
    Event verifyWebhook(String payload, String signature) throws Exception;
}
