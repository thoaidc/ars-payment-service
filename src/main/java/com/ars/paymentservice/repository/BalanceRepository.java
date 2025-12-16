package com.ars.paymentservice.repository;

import com.ars.paymentservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Integer> {
    Optional<Balance> findByTypeAndRefId(Integer type, Integer refId);
}
