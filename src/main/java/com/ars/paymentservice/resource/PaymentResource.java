package com.ars.paymentservice.resource;

import com.ars.paymentservice.dto.request.SearchPaymentHistoriesRequestDTO;
import com.ars.paymentservice.service.PaymentService;
import com.dct.model.dto.request.BaseRequestDTO;
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

    @GetMapping("/v1/payments/revenues/last-seven-day")
    public BaseResponseDTO getRevenueLastSevenDay(@RequestParam Integer receiverId) {
        return paymentService.getRevenueLastSevenDayForShop(receiverId);
    }

    @GetMapping("/v1/payments/revenues/today")
    public BaseResponseDTO getRevenueToDayForShop(@RequestParam Integer receiverId) {
        return paymentService.getRevenueToDayForShop(receiverId);
    }

    @GetMapping("/v1/payments/revenues/admin/last-seven-day")
    public BaseResponseDTO getRevenueLastSevenDayForAdmin() {
        return paymentService.getRevenueLastSevenDayForAdmin();
    }

    @GetMapping("/v1/payments/revenues/admin/today")
    public BaseResponseDTO getRevenueToDayForAdmin() {
        return paymentService.getRevenueToDayForAdmin();
    }

    @GetMapping("/v1/payments/finances/statistic/shop")
    public BaseResponseDTO getFinanceStatisticForShop(@ModelAttribute BaseRequestDTO requestDTO) {
        return paymentService.getFinanceStatistic(requestDTO, false);
    }

    @GetMapping("/v1/payments/finances/statistic/admin")
    public BaseResponseDTO getFinanceStatisticForAdmin(@ModelAttribute BaseRequestDTO requestDTO) {
        return paymentService.getFinanceStatistic(requestDTO, true);
    }
}
