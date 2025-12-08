package com.ars.paymentservice.service;

import com.ars.paymentservice.dto.request.CreateQrRequestDTO;
import com.dct.model.dto.response.BaseResponseDTO;

public interface MoneyTransferService {
    BaseResponseDTO createQrPayment(CreateQrRequestDTO requestDTO);
}
