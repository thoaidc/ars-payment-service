package com.ars.paymentservice.integration;

import com.ars.paymentservice.properties.PayOSProperties;
import com.ars.paymentservice.properties.VNPayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.payos.PayOS;
import vn.payos.core.ClientOptions;

@Configuration
@EnableConfigurationProperties({VNPayProperties.class, PayOSProperties.class})
public class BankIntegrationConfiguration {
    private final PayOSProperties payOSProperties;

    public BankIntegrationConfiguration(PayOSProperties payOSProperties) {
        this.payOSProperties = payOSProperties;
    }

    @Bean
    public PayOS payOS() {
        ClientOptions options = ClientOptions.builder()
                .clientId(payOSProperties.getMoneyReceiveConfig().getClientId())
                .apiKey(payOSProperties.getMoneyReceiveConfig().getApiKey())
                .checksumKey(payOSProperties.getMoneyReceiveConfig().getChecksumKey())
                .logLevel(payOSProperties.getMoneyReceiveConfig().getLogLevel())
                .build();
        return new PayOS(options);
    }

    @Bean
    public PayOS payOSPayout() {
        ClientOptions options = ClientOptions.builder()
                .clientId(payOSProperties.getMoneyCheckoutConfig().getClientId())
                .apiKey(payOSProperties.getMoneyCheckoutConfig().getApiKey())
                .checksumKey(payOSProperties.getMoneyCheckoutConfig().getChecksumKey())
                .logLevel(payOSProperties.getMoneyCheckoutConfig().getLogLevel())
                .build();
        return new PayOS(options);
    }
}
