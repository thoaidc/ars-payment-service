package com.ars.paymentservice.repository;

import com.ars.paymentservice.dto.mapping.PaymentGatewayResponse;
import com.ars.paymentservice.entity.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, Integer> {
    @Query(value = "SELECT id, name, code FROM payment_gateway ORDER BY name", nativeQuery = true)
    List<PaymentGatewayResponse> getPaymentGateways();

    @Query(value = "SELECT id FROM payment_gateway WHERE code = ?", nativeQuery = true)
    Optional<Integer> findIdByCode(String paymentGatewayCode);
}
