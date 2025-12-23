package com.ars.paymentservice.service.impl;

import com.ars.paymentservice.entity.OutBox;
import com.ars.paymentservice.queue.publisher.KafkaProducer;
import com.ars.paymentservice.repository.OutBoxRepository;
import com.ars.paymentservice.service.OutBoxService;
import com.dct.model.constants.BaseOutBoxConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class OutBoxServiceImpl implements OutBoxService {
    private final KafkaProducer kafkaProducer;
    private final OutBoxRepository outBoxRepository;
    private static final Logger log = LoggerFactory.getLogger(OutBoxServiceImpl.class);

    public OutBoxServiceImpl(KafkaProducer kafkaProducer, OutBoxRepository outBoxRepository) {
        this.kafkaProducer = kafkaProducer;
        this.outBoxRepository = outBoxRepository;
    }

    @Override
    @Transactional
    public void processOutBoxEvent() {
        List<OutBox> outBoxes = outBoxRepository.findTopOutBoxesByStatus(BaseOutBoxConstants.Status.PENDING);
        outBoxes = outBoxes.stream().filter(Objects::nonNull).toList();
        outBoxes.forEach(this::sendOutBoxMessage);
        outBoxRepository.saveAll(outBoxes);
    }

    private void sendOutBoxMessage(OutBox outBox) {
        log.info("[SEND_EVENT_FROM_OUTBOX] - refId: {}, type: {}", outBox.getRefId(), outBox.getType());

        try {
            switch (outBox.getType()) {
                case BaseOutBoxConstants.Type.ORDER_PAYMENT_SUCCESSFUL ->
                        kafkaProducer.sendMessagePaymentSuccessful(outBox.getValue());
                case BaseOutBoxConstants.Type.ORDER_PAYMENT_FAILURE ->
                        kafkaProducer.sendMessagePaymentFailure(outBox.getValue());
            }

            outBox.setStatus(BaseOutBoxConstants.Status.COMPLETION);
        } catch (Exception e) {
            log.error("[SEND_OUTBOX_MESSAGE_ERROR] - refId: {}, type: {}", outBox.getRefId(), outBox.getType(), e);
            outBox.setStatus(BaseOutBoxConstants.Status.FAILED);
            outBox.setError(e.getMessage());
        }
    }
}
