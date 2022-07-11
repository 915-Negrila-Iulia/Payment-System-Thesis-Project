package com.example.backend.service;

import com.example.backend.model.Account;
import com.example.backend.model.AccountHistory;

import java.util.List;

public interface IAccountHistoryService {
    AccountHistory saveAccountHistory(Account accountHistory);
    List<AccountHistory> getHistoryOfAccounts();
}
