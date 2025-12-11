package com.ars.paymentservice.queue.listener;

import com.ars.paymentservice.service.PaymentService;
import com.dct.model.common.JsonUtils;
import com.dct.model.constants.BaseKafkaConstants;
import com.dct.model.event.OrderCreatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentRequestKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentRequestKafkaListener.class);
    private final PaymentService paymentService;

    public PaymentRequestKafkaListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
        topics = BaseKafkaConstants.Topic.ORDER_CREATED,
        groupId = BaseKafkaConstants.GroupId.ORDER_CREATED,
        concurrency = BaseKafkaConstants.Consumers.ORDER_CREATED
    )
    public void receiveMessage(
        @Payload String payload,
        @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String ignoredKey,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int ignoredPartition,
        Acknowledgment ack
    ) {
        log.info("[HANDLE_ORDER_PAYMENT_REQUEST_REQUEST] - message payload: {}", payload);

        if (Objects.isNull(payload)) {
            log.error("[HANDLE_ORDER_PAYMENT_REQUEST_FAILED] - message payload is null");
            ack.acknowledge();
            return;
        }

        OrderCreatedEvent orderCreatedEvent = null;

        try {
            orderCreatedEvent = JsonUtils.parseJson(payload, OrderCreatedEvent.class);

            if (Objects.isNull(orderCreatedEvent) || Objects.isNull(orderCreatedEvent.getOrderId())) {
                log.error("[HANDLE_ORDER_PAYMENT_REQUEST_FAILED] - Order payment request content or orderId is null");
                ack.acknowledge();
                return;
            }

            paymentService.createPayment(orderCreatedEvent);
        } catch (Exception e) {
            log.error("[HANDLE_ORDER_PAYMENT_REQUEST_ERROR] - Order payment request error. {}", e.getMessage(), e);

            if (Objects.nonNull(orderCreatedEvent)) {
                paymentService.cancelOrderPaymentRequest(orderCreatedEvent, e.getMessage());
            }
        }

        ack.acknowledge();
    }
}
