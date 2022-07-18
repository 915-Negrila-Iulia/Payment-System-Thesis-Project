package com.example.backend.services.interfaces;

import com.example.backend.models.Account;
import com.example.backend.models.AccountHistory;
import com.example.backend.models.Person;
import com.example.backend.models.PersonHistory;

import java.util.List;
import java.util.Optional;

public interface IAccountService {

    Account saveAccount(Account account);

    Optional<Account> findAccountById(Long id);

    List<Account> getAllAccounts();

    Account undoneAccountChanges(Account account, AccountHistory lastVersion);

    List<AccountHistory> getHistoryOfAccounts();

    List<AccountHistory> getHistoryByAccountId(Long accountId);

    Account createAccount(Account account, Long currentUserId);

    Account updateAccount(Long id, Long currentUserId, Account account);

    Account approveAccount(Long id, Long currentUserId);

    Account rejectAccount(Long id, Long currentUserId);

    Account deleteAccount(Long id, Long currentUserId);
}
