package com.ars.paymentservice.dto.response;

import java.math.BigDecimal;

public class FinanceStatisticDTO {
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private BigDecimal totalPlatformFee = BigDecimal.ZERO;
    private BigDecimal totalProfit = BigDecimal.ZERO;

    public FinanceStatisticDTO() {}

    public FinanceStatisticDTO(BigDecimal totalRevenue, BigDecimal totalProfit) {
        this.totalRevenue = totalRevenue;
        this.totalProfit = totalProfit;
    }

    public FinanceStatisticDTO(BigDecimal totalRevenue, BigDecimal totalPlatformFee, BigDecimal totalProfit) {
        this.totalRevenue = totalRevenue;
        this.totalPlatformFee = totalPlatformFee;
        this.totalProfit = totalProfit;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalPlatformFee() {
        return totalPlatformFee;
    }

    public void setTotalPlatformFee(BigDecimal totalPlatformFee) {
        this.totalPlatformFee = totalPlatformFee;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }
}
