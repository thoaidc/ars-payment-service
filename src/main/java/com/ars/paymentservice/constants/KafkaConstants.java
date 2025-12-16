package com.ars.paymentservice.constants;

public interface KafkaConstants {
    interface Consumers {
        String CHANGE_BALANCE_AMOUNT = "1";
    }

    interface GroupId {
        String CHANGE_BALANCE_AMOUNT = "change-balance-amount-group-id";
    }

    interface Topic {
        String CHANGE_BALANCE_AMOUNT = "${spring.kafka.topics.change-balance-amount}";
    }
}
