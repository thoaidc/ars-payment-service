package com.ars.paymentservice.service.impl;

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
import com.dct.model.common.JsonUtils;
import com.dct.model.constants.BaseOutBoxConstants;
import com.dct.model.dto.response.BaseResponseDTO;
import com.dct.model.event.OrderCreatedEvent;
import com.dct.model.event.PaymentFailureEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
                              BankIntegrationFactory bankIntegrationFactory, OutBoxRepository outBoxRepository) {
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
            paymentRequest.setTransNumberId((long) orderRequest.getUserId() + orderRequest.getOrderId());
            paymentRequest.setTransId(String.valueOf(paymentRequest.getTransNumberId()));
            paymentRequest.setAmount(orderRequest.getAmount());
            paymentRequest.setPaymentContent("Thanh toan don hang: " + orderRequest.getOrderId());
            PaymentHistory paymentHistory = new PaymentHistory();
            paymentHistory.setAmount(orderRequest.getAmount());
            paymentHistory.setPaymentMethod(orderRequest.getPaymentMethod());
            Optional<Integer> paymentGatewayId = paymentGatewayRepository.findIdByCode(orderRequest.getPaymentMethod());
            paymentHistory.setPaymentGatewayId(paymentGatewayId.orElse(0));
            paymentHistory.setType(PaymentConstants.Type.PAY_FOR_ORDER);
            paymentHistory.setRefId(orderRequest.getOrderId());
            paymentHistory.setTransId(paymentRequest.getTransId());
            paymentHistory.setStatus(PaymentConstants.Status.PENDING);
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
    public void cancelOrderPaymentRequest(OrderCreatedEvent orderRequest, String errorMessage) {
        PaymentFailureEvent paymentFailureEvent = new PaymentFailureEvent();
        paymentFailureEvent.setOrderId(orderRequest.getOrderId());
        paymentFailureEvent.setUserId(orderRequest.getUserId());
        paymentFailureEvent.setErrorCode(BaseOutBoxConstants.Type.ORDER_PAYMENT_FAILURE);
        paymentFailureEvent.setErrorMessage(errorMessage);
        OutBox outBox = new OutBox();
        outBox.setSagaId(orderRequest.getSagaId());
        outBox.setType(BaseOutBoxConstants.Type.ORDER_PAYMENT_FAILURE);
        outBox.setStatus(BaseOutBoxConstants.Status.PENDING);
        outBox.setValue(JsonUtils.toJsonString(paymentFailureEvent));
        outBoxRepository.save(outBox);
    }
}
