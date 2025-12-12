package com.ars.paymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdatePayOSWebhookUrl {
    @NotBlank
    private String webhookUrl;

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
}
