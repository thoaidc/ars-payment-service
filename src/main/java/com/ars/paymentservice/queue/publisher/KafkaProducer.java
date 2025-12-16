package com.ars.paymentservice.queue.publisher;

import com.ars.paymentservice.dto.request.ChangeBalanceAmountEvent;
import com.dct.model.common.JsonUtils;
import com.dct.model.config.properties.KafkaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(KafkaTemplate.class)
public class KafkaProducer {
    private final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    @Value("${spring.kafka.topics.change-balance-amount}")
    private String changeBalanceAmountTopic;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, KafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    public void sendMessagePaymentSuccessful(String event) {
        log.info("[SEND_PAYMENT_SUCCESSFUL_TOPIC] - {}", event);
        kafkaTemplate.send(kafkaProperties.getTopics().getOrderPaymentSuccessful(), event);
    }

    public void sendMessagePaymentFailure(String event) {
        log.info("[SEND_PAYMENT_FAILURE_TOPIC] - {}", event);
        kafkaTemplate.send(kafkaProperties.getTopics().getOrderPaymentFailure(), event);
    }

    public void sendMessageChangeBalanceAmount(ChangeBalanceAmountEvent changeBalanceAmountEvent) {
        log.info("[SEND_CHANGE_BALANCE_AMOUNT_TOPIC] - {}", changeBalanceAmountEvent);
        kafkaTemplate.send(changeBalanceAmountTopic, JsonUtils.toJsonString(changeBalanceAmountEvent));
    }
}
