package com.ars.paymentservice.client;

import com.ars.paymentservice.dto.request.MessageDTO;
import com.dct.model.dto.response.BaseResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationServiceClient {
    @PostMapping("/api/internal/v1/messages")
    BaseResponseDTO sendSocketNotification(@RequestBody MessageDTO message);
}
