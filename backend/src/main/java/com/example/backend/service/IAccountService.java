package com.example.backend.service;

import com.example.backend.model.Account;
import com.example.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface IAccountService {
    Account saveAccount(Account account);
    Optional<Account> findAccountById(Long id);
    List<Account> getAllAccounts();
}
