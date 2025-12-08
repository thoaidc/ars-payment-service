package com.ars.paymentservice.properties;

import com.ars.paymentservice.constants.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = PropertiesConstants.VNP_SECRET_CONFIG)
public class VNPayProperties {
    private String VNP_API_VERSION;
    private String VNP_TMN_CODE;
    private String VNP_RETURN_URL;
    private String VNP_SECURE_HASH;
    private String VNP_PAYMENT_URL;

    public String getVNP_API_VERSION() {
        return VNP_API_VERSION;
    }

    public void setVNP_API_VERSION(String VNP_API_VERSION) {
        this.VNP_API_VERSION = VNP_API_VERSION;
    }

    public String getVNP_TMN_CODE() {
        return VNP_TMN_CODE;
    }

    public void setVNP_TMN_CODE(String VNP_TMN_CODE) {
        this.VNP_TMN_CODE = VNP_TMN_CODE;
    }

    public String getVNP_RETURN_URL() {
        return VNP_RETURN_URL;
    }

    public void setVNP_RETURN_URL(String VNP_RETURN_URL) {
        this.VNP_RETURN_URL = VNP_RETURN_URL;
    }

    public String getVNP_SECURE_HASH() {
        return VNP_SECURE_HASH;
    }

    public void setVNP_SECURE_HASH(String VNP_SECURE_HASH) {
        this.VNP_SECURE_HASH = VNP_SECURE_HASH;
    }

    public String getVNP_PAYMENT_URL() {
        return VNP_PAYMENT_URL;
    }

    public void setVNP_PAYMENT_URL(String VNP_PAYMENT_URL) {
        this.VNP_PAYMENT_URL = VNP_PAYMENT_URL;
    }
}
