package com.example.backend.service;

import com.example.backend.model.Account;
import com.example.backend.model.AccountHistory;
import com.example.backend.model.UserHistory;
import com.example.backend.repository.IAccountHistoryRepository;
import com.example.backend.repository.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AccountHistoryService implements IAccountHistoryService{

    @Autowired
    IAccountHistoryRepository accountHistoryRepository;

    @Override
    public AccountHistory saveAccountHistory(Account account) {
        AccountHistory accountHistory = new AccountHistory(account.getIban(), account.getCountryCode(),
                account.getBankCode(), account.getCurrency(), account.getPersonID(), account.getStatus(),
                account.getNextStatus(), account.getAccountStatus(), account.getId());
        return accountHistoryRepository.save(accountHistory);
    }

    @Override
    public List<AccountHistory> getHistoryByAccountId(Long accountId){
        return accountHistoryRepository.findAll().stream()
                .filter(accountHistory -> Objects.equals(accountHistory.getAccountID(), accountId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountHistory> getHistoryOfAccounts() {
        return accountHistoryRepository.findAll();
    }
}
