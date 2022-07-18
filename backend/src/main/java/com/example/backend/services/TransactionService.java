package com.example.backend.services;

import com.example.backend.models.enumerations.ActionTransactionEnum;
import com.example.backend.models.enumerations.StatusEnum;
import com.example.backend.models.Transaction;
import com.example.backend.models.enumerations.TypeTransactionEnum;
import com.example.backend.repositories.ITransactionRepository;
import com.example.backend.services.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction depositTransaction(Long accountId, Double amount){
        Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.DEPOSIT, amount,
                accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
        transactionRepository.save(transaction);
        return transaction;
    }

    @Override
    public Transaction withdrawalTransaction(Long accountId, Double amount) {
        Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.WITHDRAWAL, amount,
                accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
        transactionRepository.save(transaction);
        return transaction;
    }

    @Override
    public Transaction transferTransaction(Long accountId, Long targetId, Double amount) {
        Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.TRANSFER, amount,
                accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
        transaction.setTargetAccountID(targetId);
        transactionRepository.save(transaction);
        return transaction;
    }


}
