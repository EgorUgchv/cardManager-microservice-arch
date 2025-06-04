package com.balanceservice.repository;

import com.balanceservice.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBalanceRepository extends JpaRepository<Balance, Integer> {
}
