package com.ars.paymentservice.resource;

import com.ars.paymentservice.dto.request.CreateQrRequestDTO;
import com.ars.paymentservice.service.MoneyTransferService;
import com.dct.model.dto.response.BaseResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/p/v1/payments")
public class MoneyTransferResource {
    private final MoneyTransferService moneyTransferService;

    public MoneyTransferResource(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    @PostMapping("/create-qr")
    public BaseResponseDTO createQrPayment(@RequestBody CreateQrRequestDTO requestDTO) {
        return moneyTransferService.createQrPayment(requestDTO);
    }
}
