package com.ars.paymentservice.dto.request;

import com.dct.model.dto.request.BaseRequestDTO;

public class SearchPaymentHistoriesRequestDTO extends BaseRequestDTO {
    private Integer shopId;
    private Integer userId;
    private String transId;

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }
}
