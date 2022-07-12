package com.example.backend.service;

import com.example.backend.model.Balance;

import java.util.List;

public interface IBalanceService {
    Balance saveBalance(Balance balance);
    Balance getCurrentBalance(Long id);
    List<Balance> getAllBalances();
    List<Balance> getAllBalancesByAccountId(Long id);
}
