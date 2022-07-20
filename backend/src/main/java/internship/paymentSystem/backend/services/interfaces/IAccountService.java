package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.models.Account;
import internship.paymentSystem.backend.models.AccountHistory;

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

    Account approveAccount(Long id, Long currentUserId) throws Exception;

    Account rejectAccount(Long id, Long currentUserId) throws Exception;

    Account deleteAccount(Long id, Long currentUserId);

}
