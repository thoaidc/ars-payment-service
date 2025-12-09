package com.ars.paymentservice.properties;

import com.ars.paymentservice.constants.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import vn.payos.core.ClientOptions;

@ConfigurationProperties(prefix = PropertiesConstants.PAY_OS_CONFIG)
public class PayOSProperties {
    private PayIn moneyReceiveConfig;
    private PayOut moneyCheckoutConfig;

    public static class PayIn {
        private String clientId;
        private String apiKey;
        private String checksumKey;
        private String returnUrl;
        private String cancelUrl;
        private ClientOptions.LogLevel logLevel;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getChecksumKey() {
            return checksumKey;
        }

        public void setChecksumKey(String checksumKey) {
            this.checksumKey = checksumKey;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getCancelUrl() {
            return cancelUrl;
        }

        public void setCancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
        }

        public ClientOptions.LogLevel getLogLevel() {
            return logLevel;
        }

        public void setLogLevel(ClientOptions.LogLevel logLevel) {
            this.logLevel = logLevel;
        }
    }

    public static class PayOut {
        private String clientId;
        private String apiKey;
        private String checksumKey;
        private String returnUrl;
        private String cancelUrl;
        private ClientOptions.LogLevel logLevel;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getChecksumKey() {
            return checksumKey;
        }

        public void setChecksumKey(String checksumKey) {
            this.checksumKey = checksumKey;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }

        public String getCancelUrl() {
            return cancelUrl;
        }

        public void setCancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
        }

        public ClientOptions.LogLevel getLogLevel() {
            return logLevel;
        }

        public void setLogLevel(ClientOptions.LogLevel logLevel) {
            this.logLevel = logLevel;
        }
    }

    public PayIn getMoneyReceiveConfig() {
        return moneyReceiveConfig;
    }

    public void setMoneyReceiveConfig(PayIn moneyReceiveConfig) {
        this.moneyReceiveConfig = moneyReceiveConfig;
    }

    public PayOut getMoneyCheckoutConfig() {
        return moneyCheckoutConfig;
    }

    public void setMoneyCheckoutConfig(PayOut moneyCheckoutConfig) {
        this.moneyCheckoutConfig = moneyCheckoutConfig;
    }
}
