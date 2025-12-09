package com.ars.paymentservice.properties;

import com.ars.paymentservice.constants.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = PropertiesConstants.VN_PAY_CONFIG)
public class VNPayProperties {
    private String apiVersion;
    private String tmnCode;
    private String returnUrl;
    private String secureHash;
    private String paymentUrl;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getTmnCode() {
        return tmnCode;
    }

    public void setTmnCode(String tmnCode) {
        this.tmnCode = tmnCode;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getSecureHash() {
        return secureHash;
    }

    public void setSecureHash(String secureHash) {
        this.secureHash = secureHash;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
}
