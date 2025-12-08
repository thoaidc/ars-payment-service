package com.ars.paymentservice.repository;

import com.ars.paymentservice.dto.mapping.PaymentGatewayResponse;
import com.ars.paymentservice.entity.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, Integer> {
    @Query(value = "SELECT id, name, code FROM payment_gateway ORDER BY name ASC", nativeQuery = true)
    List<PaymentGatewayResponse> getPaymentGateways();
}
