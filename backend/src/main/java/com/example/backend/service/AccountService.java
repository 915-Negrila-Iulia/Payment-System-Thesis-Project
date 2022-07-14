package com.example.backend.service;

import com.example.backend.model.Account;
import com.example.backend.model.AccountHistory;
import com.example.backend.model.StatusEnum;
import com.example.backend.repository.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements IAccountService{

    @Autowired
    IAccountRepository accountRepository;

    @Override
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> findAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account undoneAccountChanges(Account account, AccountHistory lastVersion) {
        account.setIban(lastVersion.getIban());
        account.setCountryCode(lastVersion.getCountryCode());
        account.setBankCode(lastVersion.getBankCode());
        account.setCurrency(lastVersion.getCurrency());
        account.setAccountStatus(lastVersion.getAccountStatus());
        account.setStatus(StatusEnum.ACTIVE);
        account.setNextStatus(StatusEnum.ACTIVE);
        return account;
    }
}
