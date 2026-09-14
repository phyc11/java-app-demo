package com.example.billing.stripe;

import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultStripeGateway implements StripeGateway {
    private final String webhookSecret;

    public DefaultStripeGateway(@Value("${stripe.secret-key:}") String secretKey,
                                @Value("${stripe.webhook-secret:}") String webhookSecret) {
        Stripe.apiKey = secretKey;
        this.webhookSecret = webhookSecret;
    }

    @Override
    public PaymentIntent createPaymentIntent(long amount, String currency, Map<String,String> metadata,
                                             String idempotencyKey) throws Exception {
        if (Stripe.apiKey == null || Stripe.apiKey.trim().isEmpty())
            throw new IllegalStateException("STRIPE_SECRET_KEY is not configured");
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount).setCurrency(currency.toLowerCase()).putAllMetadata(metadata)
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                .build();
        RequestOptions options = RequestOptions.builder().setIdempotencyKey(idempotencyKey).build();
        return PaymentIntent.create(params, options);
    }

    @Override
    public Event verifyWebhook(String payload, String signature) throws Exception {
        if (webhookSecret == null || webhookSecret.trim().isEmpty())
            throw new IllegalStateException("STRIPE_WEBHOOK_SECRET is not configured");
        return Webhook.constructEvent(payload, signature, webhookSecret);
    }
}
