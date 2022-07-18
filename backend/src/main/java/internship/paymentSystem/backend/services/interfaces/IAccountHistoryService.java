package com.example.backend.services.interfaces;

import com.example.backend.models.Account;
import com.example.backend.models.AccountHistory;

import java.util.List;

public interface IAccountHistoryService {

    AccountHistory saveAccountHistory(Account accountHistory);

    List<AccountHistory> getHistoryOfAccounts();

    List<AccountHistory> getHistoryByAccountId(Long id);

    AccountHistory getLastVersionOfAccount(Long id);

}
