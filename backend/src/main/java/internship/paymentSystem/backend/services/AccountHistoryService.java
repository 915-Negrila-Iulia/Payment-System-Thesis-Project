package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.models.Account;
import internship.paymentSystem.backend.models.AccountHistory;
import internship.paymentSystem.backend.models.Person;
import internship.paymentSystem.backend.models.PersonHistory;
import internship.paymentSystem.backend.repositories.IAccountHistoryRepository;
import internship.paymentSystem.backend.services.interfaces.IAccountHistoryService;
import internship.paymentSystem.backend.services.interfaces.IPersonHistoryService;
import internship.paymentSystem.backend.services.interfaces.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AccountHistoryService implements IAccountHistoryService {

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
    public List<AccountHistory> getAccountHistoryByPersonId(Long personId){
        return accountHistoryRepository.findAll().stream()
                .filter(account -> Objects.equals(account.getPersonID(), personId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountHistory> getHistoryOfAccounts() {
        return accountHistoryRepository.findAll();
    }

    @Override
    public AccountHistory getLastVersionOfAccount(Long id) {
        List<AccountHistory> history = this.getHistoryByAccountId(id);
        AccountHistory findLastVersion =
                history.stream().max(Comparator.comparing(AccountHistory::getTimestamp)).isPresent() ?
                        history.stream().max(Comparator.comparing(AccountHistory::getTimestamp)).get() : null;
        return findLastVersion != null ? new AccountHistory(findLastVersion.getIban(), findLastVersion.getCountryCode(),
                findLastVersion.getBankCode(), findLastVersion.getCurrency(), findLastVersion.getAccountStatus()) : null;
    }

    @Override
    public List<AccountHistory> getAccountState(LocalDateTime timestamp){
        return accountHistoryRepository.findAll().stream()
                .filter(account -> account.getTimestamp().isEqual(timestamp)).collect(Collectors.toList());
    }

}
