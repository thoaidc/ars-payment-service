package com.ars.paymentservice.resource;

import com.ars.paymentservice.service.BalanceService;
import com.dct.model.dto.response.BaseResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balance")
public class BalanceResource {
    private final BalanceService balanceService;

    public BalanceResource(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/shop")
    public BaseResponseDTO getBalanceForShop() {
        return balanceService.getBalanceForShop();
    }

    @GetMapping("/admin")
    public BaseResponseDTO getBalanceForAdmin() {
        return balanceService.getBalanceForAdmin();
    }
}
