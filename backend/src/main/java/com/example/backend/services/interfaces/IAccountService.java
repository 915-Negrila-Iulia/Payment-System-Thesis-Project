package com.example.backend.services.interfaces;

import com.example.backend.models.Account;
import com.example.backend.models.AccountHistory;

import java.util.List;
import java.util.Optional;

public interface IAccountService {

    Account saveAccount(Account account);

    Optional<Account> findAccountById(Long id);

    List<Account> getAllAccounts();

    Account undoneAccountChanges(Account account, AccountHistory lastVersion);

}
