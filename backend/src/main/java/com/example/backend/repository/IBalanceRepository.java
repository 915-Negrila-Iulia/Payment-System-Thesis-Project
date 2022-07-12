package com.example.backend.repository;

import com.example.backend.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBalanceRepository extends JpaRepository<Balance, Long> {

}
