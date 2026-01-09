package com.ars.paymentservice.client;

import com.ars.paymentservice.dto.request.UserIDRequest;
import com.dct.model.dto.response.BaseResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {
    @PostMapping("/api/internal/users/by-ids")
    BaseResponseDTO getPaymentHistoryUser(@RequestBody UserIDRequest ids);
}
