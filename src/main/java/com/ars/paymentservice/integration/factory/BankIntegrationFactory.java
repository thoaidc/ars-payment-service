package com.ars.paymentservice.integration.factory;

import com.ars.paymentservice.integration.IBankIntegration;
import com.dct.model.exception.BaseIllegalArgumentException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BankIntegrationFactory {
    private final Map<String, IBankIntegration> bankIntegrationMap;
    private static final String ENTITY_NAME = "com.ars.paymentservice.integration.factory.BankIntegrationFactory";

    public BankIntegrationFactory(List<IBankIntegration> bankServiceIntegrations) {
        this.bankIntegrationMap = bankServiceIntegrations.stream()
                .collect(Collectors.toMap(IBankIntegration::getType, instance -> instance));
    }

    public IBankIntegration getBankIntegration(String bankType) {
        IBankIntegration bankIntegrationBean = bankIntegrationMap.get(bankType);

        if (Objects.isNull(bankIntegrationBean)) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, bankType + " do not supports");
        }

        return bankIntegrationBean;
    }
}
