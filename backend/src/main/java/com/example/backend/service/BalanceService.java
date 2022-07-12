package com.example.backend.service;

import com.example.backend.model.Balance;
import com.example.backend.repository.IBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BalanceService implements IBalanceService{

    @Autowired
    IBalanceRepository balanceRepository;

    @Override
    public Balance saveBalance(Balance balance) {
        return balanceRepository.save(balance);
    }

    @Override
    public List<Balance> getAllBalances() {
        return balanceRepository.findAll();
    }

    @Override
    public List<Balance> getAllBalancesByAccountId(Long id){
        return balanceRepository.findAll().stream()
                .filter(balance -> Objects.equals(balance.getAccountID(), id))
                .collect(Collectors.toList());
    }

    @Override
    public Balance getCurrentBalance(Long id){
        return this.getAllBalancesByAccountId(id).stream()
                .reduce((previous, current) -> current)
                .orElse(null);
    }
}
