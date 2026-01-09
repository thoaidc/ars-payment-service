package com.ars.paymentservice.service.impl;

import com.ars.paymentservice.client.NotificationServiceClient;
import com.ars.paymentservice.client.ProductServiceClient;
import com.ars.paymentservice.client.UserServiceClient;
import com.ars.paymentservice.constants.PayOSConstants;
import com.ars.paymentservice.constants.PaymentConstants;
import com.ars.paymentservice.dto.mapping.PaymentGatewayResponse;
import com.ars.paymentservice.dto.mapping.RevenueDataMapping;
import com.ars.paymentservice.dto.request.MessageDTO;
import com.ars.paymentservice.dto.request.PaymentRequestDTO;
import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.dto.request.UserIDRequest;
import com.ars.paymentservice.dto.response.FinanceStatisticDTO;
import com.ars.paymentservice.dto.response.PayOSPaymentInfo;
import com.ars.paymentservice.dto.response.PaymentHistoryDTO;
import com.ars.paymentservice.dto.response.PaymentHistoryUserDTO;
import com.ars.paymentservice.entity.OutBox;
import com.ars.paymentservice.entity.PaymentHistory;
import com.ars.paymentservice.integration.IBankIntegration;
import com.ars.paymentservice.integration.factory.BankIntegrationFactory;
import com.ars.paymentservice.repository.OutBoxRepository;
import com.ars.paymentservice.repository.PaymentGatewayRepository;
import com.ars.paymentservice.repository.PaymentHistoryRepository;
import com.ars.paymentservice.service.PaymentService;

import com.dct.config.common.Common;
import com.dct.model.common.DateUtils;
import com.dct.model.common.JsonUtils;
import com.dct.model.constants.BaseDatetimeConstants;
import com.dct.model.constants.BaseOutBoxConstants;
import com.dct.model.constants.BasePaymentConstants;
import com.dct.model.dto.auth.BaseUserDTO;
import com.dct.model.dto.request.BaseRequestDTO;
import com.dct.model.dto.response.BaseResponseDTO;
import com.dct.model.event.OrderCreatedEvent;
import com.dct.model.event.PaymentFailureEvent;
import com.dct.model.event.PaymentSuccessEvent;
import com.dct.model.exception.BaseBadRequestException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final String ENTITY_NAME = "com.ars.paymentservice.service.impl.PaymentServiceImpl";
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final BankIntegrationFactory bankIntegrationFactory;
    private final NotificationServiceClient notificationServiceClient;
    private final OutBoxRepository outBoxRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final ObjectMapper objectMapper;

    public PaymentServiceImpl(PaymentGatewayRepository paymentGatewayRepository,
                              PaymentHistoryRepository paymentHistoryRepository,
                              BankIntegrationFactory bankIntegrationFactory,
                              OutBoxRepository outBoxRepository,
                              NotificationServiceClient notificationServiceClient, UserServiceClient userServiceClient, ProductServiceClient productServiceClient, ObjectMapper objectMapper) {
        this.paymentGatewayRepository = paymentGatewayRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.bankIntegrationFactory = bankIntegrationFactory;
        this.outBoxRepository = outBoxRepository;
        this.notificationServiceClient = notificationServiceClient;
        this.userServiceClient = userServiceClient;
        this.productServiceClient = productServiceClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public BaseResponseDTO getPaymentGateways() {
        List<PaymentGatewayResponse> paymentGatewayResponses = paymentGatewayRepository.getPaymentGateways();
        return BaseResponseDTO.builder().total((long) paymentGatewayResponses.size()).ok(paymentGatewayResponses);
    }

    @Override
    public BaseResponseDTO getPaymentHistoriesWithPaging(SearchPaymentHistoriesRequestDTO requestDTO) {
        Page<PaymentHistoryDTO> paymentHistoryPage = paymentHistoryRepository.getAllWithPaging(requestDTO);
        List<PaymentHistoryDTO> paymentHistories = paymentHistoryPage.getContent();
        List<Integer> userIds = paymentHistories.stream()
                .filter(pm -> pm.getType().equals(BasePaymentConstants.PaymentType.CUSTOMER_PAY_ORDER))
                .map(PaymentHistoryDTO::getUserId)
                .toList();
        List<Integer> shopIds = paymentHistories.stream()
                .filter(pm -> pm.getType().equals(BasePaymentConstants.PaymentType.SYSTEM_COLLECT_FEE))
                .map(PaymentHistoryDTO::getUserId)
                .toList();
        BaseResponseDTO userRes = userServiceClient.getPaymentHistoryUser(new UserIDRequest(userIds));
        BaseResponseDTO shopRes = productServiceClient.getPaymentHistoryUser(new UserIDRequest(shopIds));
        TypeReference<List<PaymentHistoryUserDTO>> typeReference = new TypeReference<>() {};
        List<PaymentHistoryUserDTO> userDTOS = objectMapper.convertValue(userRes.getResult(), typeReference);
        List<PaymentHistoryUserDTO> shopDTOS = objectMapper.convertValue(shopRes.getResult(), typeReference);
        Map<Integer, PaymentHistoryUserDTO> userDTOMap = userDTOS.stream().collect(
            Collectors.toMap(PaymentHistoryUserDTO::getId, Function.identity())
        );
        Map<Integer, PaymentHistoryUserDTO> shopDTOMap = shopDTOS.stream().collect(
            Collectors.toMap(PaymentHistoryUserDTO::getId, Function.identity())
        );
        paymentHistories.forEach(pm -> {
            if (pm.getType().equals(BasePaymentConstants.PaymentType.CUSTOMER_PAY_ORDER)) {
                PaymentHistoryUserDTO paymentHistoryUserDTO = userDTOMap.get(pm.getUserId());
                pm.setUsername(paymentHistoryUserDTO.getName());
            }

            if (pm.getType().equals(BasePaymentConstants.PaymentType.SYSTEM_COLLECT_FEE)) {
                PaymentHistoryUserDTO paymentHistoryUserDTO = shopDTOMap.get(pm.getUserId());
                pm.setUsername(paymentHistoryUserDTO.getName());
            }
        });
        return BaseResponseDTO.builder().total(paymentHistoryPage.getTotalElements()).ok(paymentHistories);
    }

    @Override
    public BaseResponseDTO getPaymentHistoryDetail(Integer paymentHistoryId) {
        Optional<PaymentHistory> paymentHistoryOptional = paymentHistoryRepository.findById(paymentHistoryId);

        if (paymentHistoryOptional.isEmpty()) {
            throw new BaseBadRequestException(ENTITY_NAME, "Payment history not found");
        }

        PaymentHistory paymentHistory = paymentHistoryOptional.get();
        PaymentHistoryDTO paymentHistoryDTO = new PaymentHistoryDTO();
        BeanUtils.copyProperties(paymentHistory, paymentHistoryDTO);
        TypeReference<List<PaymentHistoryUserDTO>> typeReference = new TypeReference<>() {};
        BaseResponseDTO userRes = null;

        if (paymentHistory.getType().equals(BasePaymentConstants.PaymentType.CUSTOMER_PAY_ORDER)) {
            userRes = userServiceClient.getPaymentHistoryUser(new UserIDRequest(List.of(paymentHistory.getUserId())));
        }

        if (paymentHistory.getType().equals(BasePaymentConstants.PaymentType.SYSTEM_COLLECT_FEE)) {
            userRes = productServiceClient.getPaymentHistoryUser(new UserIDRequest(List.of(paymentHistory.getUserId())));
        }

        if (userRes != null) {
            List<PaymentHistoryUserDTO> userDTOS = objectMapper.convertValue(userRes.getResult(), typeReference);
            Map<Integer, PaymentHistoryUserDTO> userDTOMap = userDTOS.stream().collect(
                    Collectors.toMap(PaymentHistoryUserDTO::getId, Function.identity())
            );
            PaymentHistoryUserDTO paymentHistoryUserDTO = userDTOMap.get(paymentHistory.getUserId());
            paymentHistoryDTO.setUsername(paymentHistoryUserDTO.getName());
        }

        return BaseResponseDTO.builder().ok(paymentHistoryDTO);
    }

    @Override
    public BaseResponseDTO getPaymentInfo(Integer orderId) {
        String paymentInfo = paymentHistoryRepository.getPaymentInfoByRefId(orderId).orElse(null);

        if (StringUtils.hasText(paymentInfo)) {
            PayOSPaymentInfo paymentData = JsonUtils.parseJson(paymentInfo, PayOSPaymentInfo.class);
            return BaseResponseDTO.builder().ok(paymentData);
        }

        throw new BaseBadRequestException(ENTITY_NAME, "Payment info not found");
    }

    @Override
    public BaseResponseDTO getRevenueLastSevenDayForShop(Integer receiverId) {
        List<RevenueDataMapping> revenueDataMappings = paymentHistoryRepository.getRevenueLastSevenDay(receiverId);
        return BaseResponseDTO.builder().ok(revenueDataMappings);
    }

    @Override
    public BaseResponseDTO getRevenueLastSevenDayForAdmin() {
        Common.checkShopAuthorities(BasePaymentConstants.SYSTEM_ACCOUNT_ID);
        List<RevenueDataMapping> revenueDataMappings = paymentHistoryRepository.getRevenueLastSevenDayForAdmin();
        return BaseResponseDTO.builder().ok(revenueDataMappings);
    }

    @Override
    public BaseResponseDTO getRevenueToDayForShop(Integer receiverId) {
        BigDecimal revenueToday = paymentHistoryRepository.getRevenueToDayForShop(receiverId);
        return BaseResponseDTO.builder().ok(Optional.ofNullable(revenueToday).orElse(BigDecimal.ZERO));
    }

    @Override
    public BaseResponseDTO getRevenueToDayForAdmin() {
        Common.checkShopAuthorities(BasePaymentConstants.SYSTEM_ACCOUNT_ID);
        BigDecimal revenueToday = paymentHistoryRepository.getRevenueToDayForAdmin();
        return BaseResponseDTO.builder().ok(Optional.ofNullable(revenueToday).orElse(BigDecimal.ZERO));
    }

    @Override
    public BaseResponseDTO getFinanceStatistic(BaseRequestDTO requestDTO, boolean forAdmin) {
        BaseUserDTO userDTO = Common.getUserWithAuthorities();
        Optional<FinanceStatisticDTO> financeStatisticDTO;

        if (forAdmin) {
            financeStatisticDTO = paymentHistoryRepository.getFinanceStatisticForAdmin(requestDTO);
        } else {
            financeStatisticDTO = paymentHistoryRepository.getFinanceStatisticForShop(userDTO.getShopId(), requestDTO);
        }

        return BaseResponseDTO.builder().ok(financeStatisticDTO.orElseGet(FinanceStatisticDTO::new));
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
            paymentHistory.setType(BasePaymentConstants.PaymentType.CUSTOMER_PAY_ORDER);
            paymentHistory.setRefId(orderRequest.getOrderId());
            paymentHistory.setUserId(orderRequest.getUserId());
            paymentHistory.setReceiverId(BasePaymentConstants.SYSTEM_ACCOUNT_ID);
            paymentHistory.setPaymentGatewayId(paymentGatewayId.orElse(BasePaymentConstants.SYSTEM_ACCOUNT_ID));
            paymentHistory.setPaymentMethod(orderRequest.getPaymentMethod());
            paymentHistory.setAmount(orderRequest.getAmount());
            paymentHistory.setStatus(PaymentConstants.Status.PENDING);
            paymentHistory.setDescription(paymentRequest.getPaymentContent());
            IBankIntegration bankService = bankIntegrationFactory.getBankIntegration(orderRequest.getPaymentMethod());
            Object paymentInfo = bankService.createPayment(paymentRequest);
            String paymentInfoStr = JsonUtils.toJsonString(paymentInfo);
            paymentHistory.setInfo(paymentInfoStr);
            paymentHistoryRepository.save(paymentHistory);
            MessageDTO messageDTO = MessageDTO.builder()
                    .topic(PaymentConstants.TOPIC_PAYMENT_NOTIFICATION + orderRequest.getUserId())
                    .content(paymentInfoStr)
                    .build();
            sendMessage(messageDTO);
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
                BasePaymentConstants.PaymentType.CUSTOMER_PAY_ORDER,
                orderId
            );

            if (paymentHistoryOptional.isEmpty()) {
                log.error("[INVALID_PAY_OS_RESPONSE] - Payment history not found for orderId: {}", orderId);
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
        refundHistory.setUserId(BasePaymentConstants.SYSTEM_ACCOUNT_ID);
        refundHistory.setReceiverId(paymentHistory.getUserId());
        refundHistory.setType(BasePaymentConstants.PaymentType.SYSTEM_REFUND_CUSTOMER);
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
        MessageDTO messageDTO = MessageDTO.builder()
                .topic(PaymentConstants.TOPIC_PAYMENT_COMPLETION_NOTIFICATION + paymentHistory.getUserId())
                .content(HttpStatus.OK.name())
                .build();
        sendMessage(messageDTO);
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
        MessageDTO messageDTO = MessageDTO.builder()
                .topic(PaymentConstants.TOPIC_PAYMENT_COMPLETION_NOTIFICATION + paymentHistory.getUserId())
                .content(paymentHistory.getError())
                .build();
        sendMessage(messageDTO);
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

    private void sendMessage(MessageDTO messageDTO) {
        try {
            notificationServiceClient.sendSocketNotification(messageDTO);
        } catch (Exception e) {
            log.error("SEND_MESSAGE_ERROR - {}", messageDTO, e);
        }
    }
}
