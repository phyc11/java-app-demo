package com.example.billing.dto;
public class StripePaymentResponse {
    private String invoiceNumber; private String paymentIntentId; private String clientSecret; private String status;
    public StripePaymentResponse() {}
    public StripePaymentResponse(String invoiceNumber,String paymentIntentId,String clientSecret,String status){this.invoiceNumber=invoiceNumber;this.paymentIntentId=paymentIntentId;this.clientSecret=clientSecret;this.status=status;}
    public String getInvoiceNumber(){return invoiceNumber;} public String getPaymentIntentId(){return paymentIntentId;}
    public String getClientSecret(){return clientSecret;} public String getStatus(){return status;}
}
