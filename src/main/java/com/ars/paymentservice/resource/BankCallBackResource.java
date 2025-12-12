package com.ars.paymentservice.resource;

import com.ars.paymentservice.dto.request.UpdatePayOSWebhookUrl;
import com.ars.paymentservice.service.PaymentService;
import com.dct.model.dto.response.BaseResponseDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.payos.PayOS;
import vn.payos.model.webhooks.ConfirmWebhookResponse;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

@RestController
@RequestMapping("/api/v1/payments/webhook")
public class BankCallBackResource {
    private static final Logger log = LoggerFactory.getLogger(BankCallBackResource.class);
    private final PaymentService paymentService;
    private final PayOS payOS;

    public BankCallBackResource(PaymentService paymentService, PayOS payOS) {
        this.paymentService = paymentService;
        this.payOS = payOS;
    }

    @PostMapping("/payos")
    public ResponseEntity<String> handleWebhook(@RequestBody Webhook webhook) {
        try {
            WebhookData data = payOS.webhooks().verify(webhook);
            paymentService.handlePayOSWebhookData(data);
            return ResponseEntity.ok(HttpStatus.OK.name());
        } catch (Exception e) {
            log.error("[HANDLE_PAY_OS_WEBHOOK_ERROR] - Could not handle response from PayOS: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/payos/webhook-url")
    public BaseResponseDTO updatePayOSWebhookUrl(@Valid @RequestBody UpdatePayOSWebhookUrl updatePayOSWebhookUrl) {
        ConfirmWebhookResponse updateWebhookResponse = payOS.webhooks().confirm(updatePayOSWebhookUrl.getWebhookUrl());
        return BaseResponseDTO.builder().ok(updateWebhookResponse);
    }
}
