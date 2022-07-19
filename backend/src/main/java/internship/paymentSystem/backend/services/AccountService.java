package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.Account;
import internship.paymentSystem.backend.models.AccountHistory;
import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.enumerations.ObjectTypeEnum;
import internship.paymentSystem.backend.models.enumerations.OperationEnum;
import internship.paymentSystem.backend.models.enumerations.StatusEnum;
import internship.paymentSystem.backend.repositories.IAccountRepository;
import internship.paymentSystem.backend.services.interfaces.IAccountHistoryService;
import internship.paymentSystem.backend.services.interfaces.IAccountService;
import internship.paymentSystem.backend.services.interfaces.IAuditService;
import internship.paymentSystem.backend.services.interfaces.IBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService implements IAccountService {

    @Autowired
    IAccountRepository accountRepository;

    @Autowired
    IAccountHistoryService accountHistoryService;

    @Autowired
    IBalanceService balanceService;

    @Autowired
    IAuditService auditService;

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

    @Override
    public List<AccountHistory> getHistoryOfAccounts() {
        return accountHistoryService.getHistoryOfAccounts();
    }

    @Override
    public List<AccountHistory> getHistoryByAccountId(Long accountId) {
        return accountHistoryService.getHistoryByAccountId(accountId);
    }

    private String ibanGenerator(String countryCode, String bankCode){
        String iban = countryCode;
        int min, max, rand;
        min = 10; max = 99;
        rand = (int)(Math.random()*(max-min+1)+min);
        iban += String.valueOf(rand);
        iban += bankCode;
        min = 1000; max = 9999;
        for(int i=0; i<4; i++){
            rand = (int)(Math.random()*(max-min+1)+min);
            iban += String.valueOf(rand);
        }
        return iban;
    }

    /**
     * Create account
     * Add a new record in 'AccountHistory' table containing the initial state of the account
     * Add a new record in 'Account' table
     * Add a new record in 'Balance' table; create initial balance of the account
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Generate random iban for the account based on country and bank codes
     * Update 'Audit' table
     * @param accountDetails details of account that is created
     * @param currentUserId id of user performing the creation
     * @return person account
     */
    @Transactional
    @Override
    public Account createAccount(Account accountDetails, Long currentUserId) {
        accountDetails.setStatus(StatusEnum.APPROVE);
        accountDetails.setNextStatus(StatusEnum.ACTIVE);
        String iban = this.ibanGenerator(accountDetails.getCountryCode(), accountDetails.getBankCode());
        accountDetails.setIban(iban);
        Account account = accountRepository.save(accountDetails);
        balanceService.createInitialBalance(account.getId());
        accountHistoryService.saveAccountHistory(accountDetails);
        Audit audit = new Audit(account.getId(), ObjectTypeEnum.ACCOUNT, OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
        return account;
    }

    /**
     * Update account
     * Check if the account exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'AccountHistory' table containing the previous state of the account
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param id of the account that is updated
     * @param currentUserId id of user performing the update
     * @param accountDetails updates to be done on the account
     * @return the updated account
     */
    @Transactional
    @Override
    public Account updateAccount(Long id, Long currentUserId, Account accountDetails) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with id " + id + " not found"));
        accountHistoryService.saveAccountHistory(account);
        account.setIban(accountDetails.getIban());
        account.setCountryCode(accountDetails.getCountryCode());
        account.setBankCode(accountDetails.getBankCode());
        account.setCurrency(accountDetails.getCurrency());
        account.setAccountStatus(accountDetails.getAccountStatus());
        account.setPersonID(accountDetails.getPersonID());
        account.setStatus(StatusEnum.APPROVE);
        account.setNextStatus(StatusEnum.ACTIVE);
        Account updatedAccount = accountRepository.save(account);
        Audit audit = new Audit(account.getId(), ObjectTypeEnum.ACCOUNT,OperationEnum.UPDATE,currentUserId);
        auditService.saveAudit(audit);
        return updatedAccount;
    }

    /**
     * Approve account's changes
     * Check if the account exists by using the given id and throw an exception otherwise
     * Check if user that wants to approve changes is not the same one that made them previously
     * Add a new record in 'AccountHistory' table containing the previous state of the account
     * Change 'Status' to 'ACTIVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param id of the account that is approved
     * @param currentUserId id of user performing the approval
     * @return the approved account
     */
    @Transactional
    @Override
    public Account approveAccount(Long id, Long currentUserId) {
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.ACCOUNT), currentUserId)) {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account with id " + id + " not found"));
            accountHistoryService.saveAccountHistory(account);
            account.setStatus(account.getNextStatus());
            Account activeAccount = accountRepository.save(account);
            Audit audit = new Audit(account.getId(), ObjectTypeEnum.ACCOUNT, OperationEnum.APPROVE, currentUserId);
            auditService.saveAudit(audit);
            return activeAccount;
        }
        return null;
    }

    /**
     * Reject account's changes
     * Check if the account exists by using the given id and throw an exception otherwise
     * Check if user that wants to reject changes is not the same one that made them previously
     * Add a new record in 'AccountHistory' table containing the previous state of the account
     * Change 'Status' to 'ACTIVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param id of the account that is rejected
     * @param currentUserId id of user performing the rejection
     * @return the rejected account
     */
    @Transactional
    @Override
    public Account rejectAccount(Long id, Long currentUserId) {
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.ACCOUNT), currentUserId)) {
            Account account = accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account with id " + id + " not found"));
            AccountHistory lastVersion = accountHistoryService.getLastVersionOfAccount(id);
            accountHistoryService.saveAccountHistory(account);
            if (accountHistoryService.getHistoryByAccountId(id).size() <= 2) {
                // account will be deleted
                account.setStatus(StatusEnum.DELETE);
                account.setNextStatus(StatusEnum.DELETE);
            } else {
                // account will have changes undone
                this.undoneAccountChanges(account, lastVersion);
            }
            Account rejectedAccount = accountRepository.save(account);
            Audit audit = new Audit(account.getId(), ObjectTypeEnum.ACCOUNT, OperationEnum.REJECT, currentUserId);
            auditService.saveAudit(audit);
            return rejectedAccount;
        }
        return null;
    }

    /**
     * Delete account
     * Account object remains stored in the database
     * Check if the account exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'AccountHistory' table containing the previous state of the account
     * Change 'Status' and 'NextStatus' to 'DELETE'
     * Update 'Audit' table
     * @param id of the account that is deleted
     * @param currentUserId id of user performing the deletion
     * @return the deleted account
     */
    @Transactional
    @Override
    public Account deleteAccount(Long id, Long currentUserId) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with id " + id + " not found"));
        accountHistoryService.saveAccountHistory(account);
        account.setStatus(StatusEnum.APPROVE);
        account.setNextStatus(StatusEnum.DELETE);
        Account deletedAccount = accountRepository.save(account);
        Audit audit = new Audit(account.getId(),ObjectTypeEnum.ACCOUNT,OperationEnum.DELETE,currentUserId);
        auditService.saveAudit(audit);
        return deletedAccount;
    }

}
