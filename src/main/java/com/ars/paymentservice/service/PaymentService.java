package com.ars.paymentservice.service;

import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.dct.model.dto.response.BaseResponseDTO;
import com.dct.model.event.OrderCreatedEvent;
import vn.payos.model.webhooks.WebhookData;

public interface PaymentService {
    BaseResponseDTO getPaymentGateways();
    BaseResponseDTO getPaymentHistoriesWithPaging(SearchPaymentHistoriesRequestDTO requestDTO);
    BaseResponseDTO getPaymentHistoryDetail(Integer paymentHistoryId);
    BaseResponseDTO getPaymentInfo(Integer orderId);
    void createPayment(OrderCreatedEvent orderCreatedEvent);
    void handlePayOSWebhookData(WebhookData payOSWebhookData);
    void cancelOrderPaymentRequest(OrderCreatedEvent orderCreatedEvent, String errorMessage);
}
