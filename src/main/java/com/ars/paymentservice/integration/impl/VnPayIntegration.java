package com.ars.paymentservice.integration.impl;

import com.ars.paymentservice.constants.PaymentConstants;
import com.ars.paymentservice.constants.VNPayConstants;
import com.ars.paymentservice.dto.request.PaymentRequestDTO;
import com.ars.paymentservice.integration.IBankIntegration;
import com.ars.paymentservice.properties.VNPayProperties;
import com.dct.model.common.DateUtils;
import com.dct.model.constants.BaseDatetimeConstants;

import com.dct.model.exception.BaseIllegalArgumentException;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service(PaymentConstants.Service.VN_PAY)
public class VnPayIntegration implements IBankIntegration {
    private static final Logger log = LoggerFactory.getLogger(VnPayIntegration.class);
    private static final String ENTITY_NAME = "com.ars.paymentservice.integration.impl.VnPayIntegration";
    private final VNPayProperties vnPayProperties;

    public VnPayIntegration(VNPayProperties vnPayProperties) {
        this.vnPayProperties = vnPayProperties;
    }

    @Override
    public String getType() {
        return PaymentConstants.Service.VN_PAY;
    }

    @Override
    public String genQR(PaymentRequestDTO paymentRequestDTO) {
        Map<String, Object> vnpParamsRequest = createMapParamsData(paymentRequestDTO);
        Map<String, String> vnpParams = new HashMap<>();

        for (Map.Entry<String, Object> entry : vnpParamsRequest.entrySet()) {
            if (Objects.nonNull(entry.getValue())) {
                vnpParams.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        // Sort parameters in ascending order of Key name (VN PAY required)
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        // Build QueryString and HashData strings
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnpParams.get(fieldName);

            if (StringUtils.hasText(fieldValue)) {
                hashData.append(fieldName).append("=").append(fieldValue).append("&");

                try {
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                            .append("=")
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII))
                            .append("&");
                } catch (Exception e) {
                    log.error("[VN_PAY_MAP_PARAMS_ERROR] - error: {}", e.getMessage());
                }
            }
        }

        // Remove the last '&' character
        String queryString = !query.isEmpty() ? query.substring(0, query.length() - 1) : "";
        String hashDataString = !hashData.isEmpty() ? hashData.substring(0, hashData.length() - 1) : "";
        String secureHash = createHmacSHA256String(vnPayProperties.getSecureHash(), hashDataString);
        return String.format("%s?%s&%s=%s",
                vnPayProperties.getPaymentUrl(),
                queryString,
                VNPayConstants.VNP_SECURE_HASH_KEY,
                secureHash);
    }

    public Map<String, Object> createMapParamsData(PaymentRequestDTO request) {
        Map<String, Object> vnpParams = new HashMap<>();
        vnpParams.put(VNPayConstants.VNP_COMMAND_KEY, VNPayConstants.VNP_COMMAND);
        vnpParams.put(VNPayConstants.VNP_CURRENCY_KEY, VNPayConstants.VNP_CURRENCY_CODE);
        vnpParams.put(VNPayConstants.VNP_VERSION_KEY, vnPayProperties.getApiVersion());
        vnpParams.put(VNPayConstants.VNP_RETURN_URL_KEY, vnPayProperties.getReturnUrl());
        vnpParams.put(VNPayConstants.VNP_TMN_CODE_KEY, vnPayProperties.getTmnCode());

        // Normalize Amount: Amount * 100 (Required)
        BigDecimal amountInCents = request.getAmount().multiply(new BigDecimal(VNPayConstants.VNP_ROUNDING_FACTOR));
        vnpParams.put(VNPayConstants.VNP_AMOUNT_KEY, amountInCents.longValue());

        // Normalize OrderInfo: Remove special characters (Required)
        String cleanOrderInfo = request.getPaymentContent().replaceAll("[^a-zA-Z0-9 ]", "").trim();
        vnpParams.put(VNPayConstants.VNP_ORDER_INFO_KEY, cleanOrderInfo);
        vnpParams.put(VNPayConstants.VNP_ORDER_TYPE_KEY, request.getVpnOrderType());

        // Make dynamic fields required
        String createDate = DateUtils.now()
            .toStringWithZoneID(BaseDatetimeConstants.ZoneID.ASIA_HO_CHI_MINH, VNPayConstants.VNP_DATE_TIME_FORMAT);
        String expiredDate = DateUtils.now()
            .plusMinutes(PaymentConstants.DEFAULT_PAYMENT_EXPIRED_DURATION)
            .toStringWithZoneID(BaseDatetimeConstants.ZoneID.ASIA_HO_CHI_MINH, VNPayConstants.VNP_DATE_TIME_FORMAT);
        String locale = Optional.ofNullable(request.getLocale()).orElse(VNPayConstants.VNP_LOCALE_VN);
        vnpParams.put(VNPayConstants.VNP_CREATED_DATE_KEY, createDate);
        vnpParams.put(VNPayConstants.VNP_EXPIRED_DATE_KEY, expiredDate);
        vnpParams.put(VNPayConstants.VNP_LOCALE_KEY, locale);

        String backCode = Optional.ofNullable(request.getVnpBankCode()).orElse(VNPayConstants.VNP_BANK_CODE_QR);
        vnpParams.put(VNPayConstants.VNP_BANK_CODE_KEY, backCode);
        vnpParams.put(VNPayConstants.VNP_TRANSACTION_REF_ID_KEY, request.getTransId());
        vnpParams.put(VNPayConstants.VNP_CUSTOMER_IP_KEY, request.getVnpCusIpAddress());
        return vnpParams;
    }

    public static String createHmacSHA256String(final String key, final String data) {
        try {
            String algorithm = HmacAlgorithms.HMAC_SHA_256.getName();
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            Formatter formatter = new Formatter();

            for (byte b : hash) {
                formatter.format("%02x", b);
            }

            return formatter.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new BaseIllegalArgumentException(ENTITY_NAME, "Error calculate HMAC: " + e.getMessage());
        }
    }
}
