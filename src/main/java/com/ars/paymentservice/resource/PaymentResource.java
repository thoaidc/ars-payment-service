package com.ars.paymentservice.resource;

import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.service.PaymentService;
import com.dct.model.dto.response.BaseResponseDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentResource {
    private final PaymentService paymentService;

    public PaymentResource(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/p/v1/payments/gateways")
    public BaseResponseDTO getPaymentGateways() {
        return paymentService.getPaymentGateways();
    }

    @GetMapping("/v1/payments/histories")
    public BaseResponseDTO getPaymentHistoriesWithPaging(@ModelAttribute SearchPaymentHistoriesRequestDTO requestDTO) {
        return paymentService.getPaymentHistoriesWithPaging(requestDTO);
    }

    @GetMapping("/v1/payments/histories/{paymentHistoryId}")
    public BaseResponseDTO getPaymentHistoryDetail(@PathVariable Integer paymentHistoryId) {
        return paymentService.getPaymentHistoryDetail(paymentHistoryId);
    }

    @GetMapping("/p/v1/payments/qr/{orderId}")
    public BaseResponseDTO getQrPaymentInfoForOrder(@PathVariable Integer orderId) {
        return paymentService.getPaymentInfo(orderId);
    }
}
