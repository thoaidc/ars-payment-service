package com.ars.paymentservice.queue.listener;

import com.ars.paymentservice.constants.KafkaConstants;
import com.ars.paymentservice.dto.request.ChangeBalanceAmountEvent;
import com.ars.paymentservice.service.BalanceService;
import com.dct.model.common.JsonUtils;

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
public class ChangeBalanceAmountKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(ChangeBalanceAmountKafkaListener.class);
    private final BalanceService balanceService;

    public ChangeBalanceAmountKafkaListener(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @KafkaListener(
        topics = KafkaConstants.Topic.CHANGE_BALANCE_AMOUNT,
        groupId = KafkaConstants.GroupId.CHANGE_BALANCE_AMOUNT,
        concurrency = KafkaConstants.Consumers.CHANGE_BALANCE_AMOUNT
    )
    public void receiveMessage(
        @Payload String payload,
        @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String ignoredKey,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int ignoredPartition,
        Acknowledgment ack
    ) {
        log.info("[HANDLE_CHANGE_BALANCE_AMOUNT_REQUEST_REQUEST] - message payload: {}", payload);

        if (Objects.isNull(payload)) {
            log.error("[HANDLE_CHANGE_BALANCE_AMOUNT_REQUEST_FAILED] - message payload is null");
            ack.acknowledge();
            return;
        }

        try {
            ChangeBalanceAmountEvent changeBalanceEvent = JsonUtils.parseJson(payload, ChangeBalanceAmountEvent.class);

            if (Objects.isNull(changeBalanceEvent) || Objects.isNull(changeBalanceEvent.getRefId())) {
                log.error("[HANDLE_CHANGE_BALANCE_AMOUNT_REQUEST_FAILED] - Request content or refId is null");
                ack.acknowledge();
                return;
            }

            balanceService.updateBalanceAmount(changeBalanceEvent);
        } catch (Exception e) {
            log.error("[HANDLE_CHANGE_BALANCE_AMOUNT_REQUEST_ERROR] - error. {}", e.getMessage(), e);
        }

        ack.acknowledge();
    }
}
