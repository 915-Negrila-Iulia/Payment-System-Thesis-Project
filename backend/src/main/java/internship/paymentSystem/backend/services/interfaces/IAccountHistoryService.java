package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.models.Account;
import internship.paymentSystem.backend.models.AccountHistory;

import java.util.List;

public interface IAccountHistoryService {

    AccountHistory saveAccountHistory(Account accountHistory);

    List<AccountHistory> getHistoryOfAccounts();

    List<AccountHistory> getHistoryByAccountId(Long id);

    AccountHistory getLastVersionOfAccount(Long id);

}
