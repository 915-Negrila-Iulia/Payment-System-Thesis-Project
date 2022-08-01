package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.models.Account;
import internship.paymentSystem.backend.models.AccountHistory;
import internship.paymentSystem.backend.models.Person;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface IAccountHistoryService {

    AccountHistory saveAccountHistory(Account accountHistory);

    List<AccountHistory> getHistoryOfAccounts();

    List<AccountHistory> getHistoryByAccountId(Long id);

    List<AccountHistory> getAccountHistoryByPersonId(Long personId);

    AccountHistory getLastVersionOfAccount(Long id);

    List<AccountHistory> getAccountState(LocalDateTime timestamp);

}
