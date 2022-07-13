package com.example.backend.service;

import com.example.backend.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface ITransactionService {
    Transaction saveTransaction(Transaction transaction);
    Optional<Transaction> findTransactionById(Long id);
    List<Transaction> getAllTransactions();
    Transaction depositTransaction(Long accountId, Double amount);
    Transaction withdrawalTransaction(Long accountId, Double amount);
    Transaction transferTransaction(Long accountId, Long targetId, Double amount);
}
