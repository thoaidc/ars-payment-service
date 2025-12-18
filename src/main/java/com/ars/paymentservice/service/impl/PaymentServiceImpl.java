package com.ars.paymentservice.service.impl;

import com.ars.paymentservice.constants.PayOSConstants;
import com.ars.paymentservice.constants.PaymentConstants;
import com.ars.paymentservice.dto.mapping.PaymentGatewayResponse;
import com.ars.paymentservice.dto.request.PaymentRequestDTO;
import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.entity.OutBox;
import com.ars.paymentservice.entity.PaymentHistory;
import com.ars.paymentservice.integration.IBankIntegration;
import com.ars.paymentservice.integration.factory.BankIntegrationFactory;
import com.ars.paymentservice.repository.OutBoxRepository;
import com.ars.paymentservice.repository.PaymentGatewayRepository;
import com.ars.paymentservice.repository.PaymentHistoryRepository;
import com.ars.paymentservice.service.PaymentService;

import com.dct.model.common.DateUtils;
import com.dct.model.common.JsonUtils;
import com.dct.model.constants.BaseDatetimeConstants;
import com.dct.model.constants.BaseOutBoxConstants;
import com.dct.model.constants.BasePaymentConstants;
import com.dct.model.dto.response.BaseResponseDTO;
import com.dct.model.event.OrderCreatedEvent;
import com.dct.model.event.PaymentFailureEvent;
import com.dct.model.event.PaymentSuccessEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final BankIntegrationFactory bankIntegrationFactory;
    private final OutBoxRepository outBoxRepository;

    public PaymentServiceImpl(PaymentGatewayRepository paymentGatewayRepository,
                              PaymentHistoryRepository paymentHistoryRepository,
                              BankIntegrationFactory bankIntegrationFactory,
                              OutBoxRepository outBoxRepository) {
        this.paymentGatewayRepository = paymentGatewayRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.bankIntegrationFactory = bankIntegrationFactory;
        this.outBoxRepository = outBoxRepository;
    }

    @Override
    public BaseResponseDTO getPaymentGateways() {
        List<PaymentGatewayResponse> paymentGatewayResponses = paymentGatewayRepository.getPaymentGateways();
        return BaseResponseDTO.builder().total((long) paymentGatewayResponses.size()).ok(paymentGatewayResponses);
    }

    @Override
    public BaseResponseDTO getPaymentHistoriesWithPaging(SearchPaymentHistoriesRequestDTO requestDTO) {
        return null;
    }

    @Override
    public BaseResponseDTO getPaymentHistoryDetail(Integer paymentHistoryId) {
        return null;
    }

    @Override
    public BaseResponseDTO getPaymentInfo(Integer orderId) {
        return null;
    }

    @Override
    @Transactional
    public void createPayment(OrderCreatedEvent orderRequest) {
        try {
            PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
            paymentRequest.setTransNumberId((long) orderRequest.getOrderId());
            paymentRequest.setAmount(orderRequest.getAmount());
            paymentRequest.setPaymentContent("Thanh toan don hang: " + orderRequest.getOrderId());
            PaymentHistory paymentHistory = new PaymentHistory();
            Optional<Integer> paymentGatewayId = paymentGatewayRepository.findIdByCode(orderRequest.getPaymentMethod());
            paymentHistory.setType(BasePaymentConstants.PaymentType.ORDER_PAYMENT);
            paymentHistory.setRefId(orderRequest.getOrderId());
            paymentHistory.setUserId(orderRequest.getUserId());
            paymentHistory.setReceiverId(PaymentConstants.SYSTEM_ACCOUNT_ID);
            paymentHistory.setPaymentGatewayId(paymentGatewayId.orElse(0));
            paymentHistory.setPaymentMethod(orderRequest.getPaymentMethod());
            paymentHistory.setAmount(orderRequest.getAmount());
            paymentHistory.setStatus(PaymentConstants.Status.PENDING);
            paymentHistory.setDescription(paymentRequest.getPaymentContent());
            IBankIntegration bankService = bankIntegrationFactory.getBankIntegration(orderRequest.getPaymentMethod());
            Object paymentInfo = bankService.createPayment(paymentRequest);
            paymentHistory.setInfo(JsonUtils.toJsonString(paymentInfo));
            paymentHistoryRepository.save(paymentHistory);
        } catch (Exception e) {
            log.error(
                "[CREATE_PAYMENT_ERROR] - orderId: {}, userId: {}, paymentMethod: {}, amount: {}. Error: {}",
                orderRequest.getOrderId(),
                orderRequest.getUserId(),
                orderRequest.getPaymentMethod(),
                orderRequest.getAmount(),
                e.getMessage()
            );
            throw e;
        }
    }

    @Override
    @Transactional
    public void handlePayOSWebhookData(WebhookData payOSWebhookData) {
        log.info(
            "[PAY_OS_WEBHOOK] - orderId: {}, error: {}, referenceCode: {}, paymentTime: {}, amount: {}",
            payOSWebhookData.getOrderCode(),
            payOSWebhookData.getCode() + " - " + payOSWebhookData.getDesc(),
            payOSWebhookData.getReference(),
            payOSWebhookData.getTransactionDateTime(),
            payOSWebhookData.getAmount()
        );

        try {
            int orderId = payOSWebhookData.getOrderCode().intValue();
            Optional<PaymentHistory> paymentHistoryOptional = paymentHistoryRepository.findByTypeAndRefId(
                BasePaymentConstants.PaymentType.ORDER_PAYMENT,
                orderId
            );

            if (paymentHistoryOptional.isEmpty()) {
                log.error("[INVALID_PAY_OS_RESPONSE] - Payment history not found");
                return;
            }

            PaymentHistory paymentHistory = paymentHistoryOptional.get();

            if (!Objects.equals(PaymentConstants.Status.PENDING, paymentHistory.getStatus())) {
                log.warn("[INVALID_PAY_OS_RESPONSE] - Duplicate webhook for payment: {}, ", paymentHistory.getId());
                return;
            }

            paymentHistoryCompletion(paymentHistory, payOSWebhookData);
        } catch (Exception e) {
            log.error("[HANDLE_PAY_OS_RESPONSE_ERROR] - error", e);
        }
    }

    private void paymentHistoryCompletion(PaymentHistory paymentHistory, WebhookData payOSWebhookData) {
        paymentHistory.setTransId(payOSWebhookData.getReference());
        String paymentTimeStr = payOSWebhookData.getTransactionDateTime();
        String paymentTimeFormat = BaseDatetimeConstants.Formatter.DEFAULT;
        paymentHistory.setPaymentTime(DateUtils.ofInstant(paymentTimeStr, paymentTimeFormat).getInstance());
        paymentHistory.setResponse(JsonUtils.toJsonString(payOSWebhookData));

        if (Objects.equals(PayOSConstants.WebhookStatus.SUCCESS, payOSWebhookData.getCode())) {
            BigDecimal customerPayAmount = BigDecimal.valueOf(payOSWebhookData.getAmount());

            if (paymentHistory.getAmount().compareTo(customerPayAmount) != 0) {
                paymentHistory.setStatus(PaymentConstants.Status.FAILURE);
                paymentHistory.setError("Invalid payment amount from customer: " + customerPayAmount);
                createRefundHistory(paymentHistory, customerPayAmount);
                cancelOrder(paymentHistory);
            } else {
                paymentHistory.setStatus(PaymentConstants.Status.SUCCESS);
                confirmOrderPaymentSuccessful(paymentHistory);
            }
        } else {
            paymentHistory.setStatus(PaymentConstants.Status.FAILURE);
            paymentHistory.setError(payOSWebhookData.getCode() + " - " + payOSWebhookData.getDesc());
            cancelOrder(paymentHistory);
        }

        paymentHistoryRepository.save(paymentHistory);
    }

    private void createRefundHistory(PaymentHistory paymentHistory, BigDecimal customerPayAmount) {
        PaymentHistory refundHistory = new PaymentHistory();
        BeanUtils.copyProperties(paymentHistory, refundHistory, "id", "transId", "paymentTime", "info");
        refundHistory.setUserId(PaymentConstants.SYSTEM_ACCOUNT_ID);
        refundHistory.setReceiverId(paymentHistory.getUserId());
        refundHistory.setType(BasePaymentConstants.PaymentType.REFUND_TO_CUSTOMER);
        refundHistory.setAmount(customerPayAmount);
        refundHistory.setStatus(PaymentConstants.Status.PENDING);
        Integer orderId = paymentHistory.getRefId();
        Integer customerId = paymentHistory.getUserId();
        String description = String.format("Hoan tien don hang: %s cho khach hang: %s", orderId, customerId);
        refundHistory.setDescription(description);
        paymentHistoryRepository.save(refundHistory);
    }

    private void confirmOrderPaymentSuccessful(PaymentHistory paymentHistory) {
        PaymentSuccessEvent paymentSuccessEvent = new PaymentSuccessEvent();
        paymentSuccessEvent.setUserId(paymentHistory.getUserId());
        paymentSuccessEvent.setOrderId(paymentHistory.getRefId());
        OutBox outBox = new OutBox();
        outBox.setRefId(paymentHistory.getRefId());
        outBox.setType(BaseOutBoxConstants.Type.ORDER_PAYMENT_SUCCESSFUL);
        outBox.setStatus(BaseOutBoxConstants.Status.PENDING);
        outBox.setValue(JsonUtils.toJsonString(paymentSuccessEvent));
        outBoxRepository.save(outBox);
    }

    private void cancelOrder(PaymentHistory paymentHistory) {
        PaymentFailureEvent paymentFailureEvent = new PaymentFailureEvent();
        paymentFailureEvent.setUserId(paymentHistory.getUserId());
        paymentFailureEvent.setOrderId(paymentHistory.getRefId());
        paymentFailureEvent.setErrorCode(BaseOutBoxConstants.Type.ORDER_PAYMENT_FAILURE);
        paymentFailureEvent.setErrorMessage(paymentHistory.getError());
        OutBox outBox = new OutBox();
        outBox.setRefId(paymentHistory.getRefId());
        outBox.setType(BaseOutBoxConstants.Type.ORDER_PAYMENT_FAILURE);
        outBox.setStatus(BaseOutBoxConstants.Status.PENDING);
        outBox.setValue(JsonUtils.toJsonString(paymentFailureEvent));
        outBoxRepository.save(outBox);
    }

    @Override
    @Transactional
    public void cancelOrderPaymentRequest(OrderCreatedEvent orderRequest, String errorMessage) {
        PaymentFailureEvent paymentFailureEvent = new PaymentFailureEvent();
        paymentFailureEvent.setUserId(orderRequest.getUserId());
        paymentFailureEvent.setOrderId(orderRequest.getOrderId());
        paymentFailureEvent.setErrorCode(BaseOutBoxConstants.Type.ORDER_PAYMENT_FAILURE);
        paymentFailureEvent.setErrorMessage(errorMessage);
        OutBox outBox = new OutBox();
        outBox.setRefId(orderRequest.getOrderId());
        outBox.setType(BaseOutBoxConstants.Type.ORDER_PAYMENT_FAILURE);
        outBox.setStatus(BaseOutBoxConstants.Status.PENDING);
        outBox.setValue(JsonUtils.toJsonString(paymentFailureEvent));
        outBoxRepository.save(outBox);
    }
}
