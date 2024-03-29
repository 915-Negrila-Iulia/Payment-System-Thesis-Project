package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.*;
import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import internship.paymentSystem.backend.models.enums.OperationEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.repositories.IAccountRepository;
import internship.paymentSystem.backend.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountService implements IAccountService {

    @Autowired
    IAccountRepository accountRepository;

    @Autowired
    IAccountHistoryService accountHistoryService;

    @Autowired
    IPersonService personService;

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
    public Set<Account> getValidAccounts(){
        return accountRepository.findAll().stream()
                .filter(account -> account.getStatus() != StatusEnum.DELETE &&
                        account.getAccountStatus() != AccountStatusEnum.CLOSED)
                .collect(Collectors.toSet());
    }

    @Override
    public Account getAccountByIban(String iban){
        return accountRepository.findAll().stream()
                .filter(account -> Objects.equals(account.getIban(), iban))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Account> getAccountByPersonId(Long personId){
        return accountRepository.findAll().stream()
                .filter(account -> Objects.equals(account.getPersonID(), personId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAccountsOfUser(Long currentUserId) {
        List<Account> userAccounts = new ArrayList<>();
        List<Person> userPersons =  personService.getPersonsOfUser(currentUserId);
        for(Person person:  userPersons){
            List<Account> accountsList = getAccountByPersonId(person.getId());
            userAccounts.addAll(accountsList);
        }
        return userAccounts;
    }

    @Override
    public List<Balance> getBalancesOfUser(Long id){
        List<Account> userAccounts = this.getAccountsOfUser(id);
        return balanceService.getAllBalances().stream()
                .filter(balance -> userAccounts.stream()
                        .anyMatch(account -> Objects.equals(account.getId(), balance.getAccountID())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAccountsByStatus(StatusEnum filterStatus) {
        return accountRepository.findAll().stream()
                .filter(account -> account.getStatus() == filterStatus)
                .collect(Collectors.toList());
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

    @Override
    public List<AccountHistory>  getAccountsHistoryOfUser(Long currentUserId) {
        List<AccountHistory> userAccountsHistory = new ArrayList<>();
        List<Person> userPersons = personService.getPersonsOfUser(currentUserId);
        for(Person person:  userPersons){
            List<AccountHistory> accountsList = accountHistoryService.getAccountHistoryByPersonId(person.getId());
            userAccountsHistory.addAll(accountsList);
        }
        return userAccountsHistory;
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
    public Account approveAccount(Long id, Long currentUserId) throws Exception {
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
        else{
            throw new Exception("Not allowed to approve");
        }
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
    public Account rejectAccount(Long id, Long currentUserId) throws Exception {
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
        else{
            throw new Exception("Not allowed to reject");
        }
    }

    /**
     * Delete account
     * Account object remains stored in the database
     * Check if the account exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'AccountHistory' table containing the previous state of the account
     * Change 'Status' and 'NextStatus' to 'DELETE'
     * Change 'AccountStatus' to 'CLOSED'
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
