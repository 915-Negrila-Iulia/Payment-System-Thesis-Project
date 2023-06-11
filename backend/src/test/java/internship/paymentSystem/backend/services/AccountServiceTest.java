package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.*;
import internship.paymentSystem.backend.models.enums.*;
import internship.paymentSystem.backend.repositories.IAccountRepository;
import internship.paymentSystem.backend.services.interfaces.IAccountHistoryService;
import internship.paymentSystem.backend.services.interfaces.IAuditService;
import internship.paymentSystem.backend.services.interfaces.IBalanceService;
import internship.paymentSystem.backend.services.interfaces.IPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    IAccountRepository accountRepository;
    @Mock
    IAccountHistoryService accountHistoryService;
    @Mock
    IPersonService personService;
    @Mock
    IBalanceService balanceService;
    @Mock
    IAuditService auditService;
    @InjectMocks
    private AccountService accountService;

    private User user;
    private Person person;
    private Account account1, account2;
    private Balance balance11, balance12, balance21;
    private AccountHistory accountHistory1;
    private Audit audit;

    @BeforeEach
    public void setup() {
        user = new User("bob", "bob@yahoo.com", "", RoleEnum.ADMIN_ROLE,
                StatusEnum.ACTIVE, StatusEnum.ACTIVE);
        user.setId(1L);

        person = new Person();
        person.setId(1L);
        person.setStatus(StatusEnum.ACTIVE);
        person.setNextStatus(StatusEnum.ACTIVE);
        person.setFirstName("Bob");
        person.setLastName("Bobbert");
        person.setAddress("Romania/Cluj-Napoca/411458/Memo10");
        person.setDateOfBirth(new Date(1997, 10, 10));
        person.setPhoneNumber("0745698412");
        person.setUserID(user.getId());

        account1 = new Account();
        account1.setId(1L);
        account1.setStatus(StatusEnum.APPROVE);
        account1.setNextStatus(StatusEnum.ACTIVE);
        account1.setAccountStatus(AccountStatusEnum.OPEN);
        account1.setIban("RO58BTRL8430952977069558");
        account1.setBankCode("BTRL");
        account1.setCountryCode("RO");
        account1.setCurrency("EUR");
        account1.setPersonID(person.getId());

        account2 = new Account();
        account2.setId(2L);
        account2.setStatus(StatusEnum.APPROVE);
        account2.setNextStatus(StatusEnum.ACTIVE);
        account2.setAccountStatus(AccountStatusEnum.OPEN);
        account2.setIban("FI43ABCD5375449011255431");
        account2.setBankCode("ABCD");
        account2.setCountryCode("FI");
        account2.setCurrency("EUR");
        account2.setPersonID(person.getId());

        balance11 = new Balance(BigDecimal.valueOf(100),BigDecimal.valueOf(100),account1.getId());
        balance12 = new Balance(BigDecimal.valueOf(100),BigDecimal.valueOf(50),account1.getId());
        balance21 = new Balance(BigDecimal.valueOf(100), BigDecimal.valueOf(100), account2.getId());

        accountHistory1 = new AccountHistory(account1);

        audit = new Audit(account1.getId(), ObjectTypeEnum.ACCOUNT, OperationEnum.CREATE, 1L);
    }

    @Test
    void testGetAllAccounts(){
        List<Account> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);

        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> allAccounts = accountService.getAllAccounts();

        assertEquals(accounts, allAccounts);
        assertEquals(allAccounts.size(), 2);

        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionsByStatus() {
        List<Account> accounts = new ArrayList<>();
        account1.setAccountStatus(AccountStatusEnum.CLOSED);
        accounts.add(account1);
        account2.setStatus(StatusEnum.DELETE);
        accounts.add(account2);
        Account validAccount = new Account();
        validAccount.setStatus(StatusEnum.ACTIVE);
        validAccount.setAccountStatus(AccountStatusEnum.OPEN);
        accounts.add(validAccount);

        when(accountRepository.findAll()).thenReturn(accounts);

        Set<Account> filteredAccounts = accountService.getValidAccounts();

        assertEquals(filteredAccounts.size(), 1);
        assertEquals(filteredAccounts.stream().iterator().next().getAccountStatus(), AccountStatusEnum.OPEN);

        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testAccountsOfUser() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);

        List<Person> persons = new ArrayList<>();
        persons.add(person);

        when(accountRepository.findAll()).thenReturn(accounts);
        when(personService.getPersonsOfUser(user.getId())).thenReturn(persons);

        List<Account> filteredAccounts = accountService.getAccountsOfUser(user.getId());

        assertEquals(filteredAccounts.size(), 2);

        verify(accountRepository, times(1)).findAll();
        verify(personService, times(1)).getPersonsOfUser(user.getId());
    }

    @Test
    void testBalancesOfUser() {
        List<Balance> balances = new ArrayList<>();
        balances.add(balance11);
        balances.add(balance12);
        balances.add(balance21);
        Balance differentUserBalance = new Balance();
        differentUserBalance.setAccountID(9999L);
        balances.add(differentUserBalance);

        List<Account> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);

        List<Person> persons = new ArrayList<>();
        persons.add(person);

        when(accountRepository.findAll()).thenReturn(accounts);
        when(personService.getPersonsOfUser(user.getId())).thenReturn(persons);
        when(balanceService.getAllBalances()).thenReturn(balances);

        List<Balance> filteredBalances = accountService.getBalancesOfUser(user.getId());

        assertEquals(filteredBalances.size(), 3);

        verify(accountRepository, times(1)).findAll();
        verify(personService, times(1)).getPersonsOfUser(user.getId());
        verify(balanceService, times(1)).getAllBalances();
    }

    @Test
    void testCreateAccount(){
        account1.setStatus(StatusEnum.DELETE);
        account1.setNextStatus(StatusEnum.DELETE);

        when(accountRepository.save(account1)).thenReturn(account1);
        when(accountHistoryService.saveAccountHistory(account1)).thenReturn(accountHistory1);
        when(auditService.saveAudit(audit)).thenReturn(audit);

        Account createdAccount = accountService.saveAccount(account1);
        AccountHistory createdAccountHistory = accountHistoryService.saveAccountHistory(account1);
        Audit createdAudit = auditService.saveAudit(audit);

        assertEquals(account1.getId(), createdAccount.getId());
        assertEquals(account1.getIban(), createdAccount.getIban());
        assertEquals(accountHistory1.getId(), createdAccountHistory.getId());
        assertEquals(accountHistory1.getAccountID(), createdAccountHistory.getAccountID());
        assertEquals(audit.getObjectID(), createdAudit.getObjectID());
        assertEquals(audit.getObjectType(), createdAudit.getObjectType());

        verify(accountRepository, times(1)).save(account1);
        verify(accountHistoryService, times(1)).saveAccountHistory(account1);
        verify(auditService, times(1)).saveAudit(audit);
    }

    @Test
    void testUpdateAccount(){
        audit.setOperation(OperationEnum.UPDATE);
        Account updateDetails = new Account();
        updateDetails.setId(account1.getId());
        updateDetails.setIban(account1.getIban());
        updateDetails.setAccountStatus(AccountStatusEnum.CLOSED);

        assertEquals(account1.getAccountStatus(), AccountStatusEnum.OPEN);

        when(accountRepository.findById(account1.getId())).thenReturn(Optional.ofNullable(account1));
        when(accountRepository.save(account1)).thenReturn(account1);
        when(accountHistoryService.saveAccountHistory(account1)).thenReturn(accountHistory1);
        when(auditService.saveAudit(audit)).thenReturn(audit);

        Account updatedAccount = accountService.updateAccount(account1.getId(), user.getId(), updateDetails);
        AccountHistory createdAccountHistory = accountHistoryService.saveAccountHistory(account1);
        Audit createdAudit = auditService.saveAudit(audit);

        assertEquals(account1.getAccountStatus(), AccountStatusEnum.CLOSED);
        assertEquals(account1.getId(), updatedAccount.getId());
        assertEquals(account1.getIban(), updatedAccount.getIban());
        assertEquals(accountHistory1.getId(), createdAccountHistory.getId());
        assertEquals(accountHistory1.getAccountID(), createdAccountHistory.getAccountID());
        assertEquals(audit.getObjectID(), createdAudit.getObjectID());
        assertEquals(audit.getObjectType(), createdAudit.getObjectType());

        verify(accountRepository, times(1)).save(account1);
        verify(accountHistoryService, times(2)).saveAccountHistory(account1);
        verify(auditService, times(2)).saveAudit(audit);
    }

    @Test
    void testApproveAccount() throws Exception {
        audit.setOperation(OperationEnum.APPROVE);
        account1.setStatus(StatusEnum.APPROVE);
        account1.setNextStatus(StatusEnum.ACTIVE);

        assertEquals(account1.getAccountStatus(), AccountStatusEnum.OPEN);

        when(accountRepository.findById(account1.getId())).thenReturn(Optional.ofNullable(account1));
        when(accountRepository.save(account1)).thenReturn(account1);
        when(accountHistoryService.saveAccountHistory(account1)).thenReturn(accountHistory1);
        when(auditService.saveAudit(audit)).thenReturn(audit);

        Account approvedAccount = accountService.approveAccount(account1.getId(), user.getId());
        AccountHistory createdAccountHistory = accountHistoryService.saveAccountHistory(account1);
        Audit createdAudit = auditService.saveAudit(audit);

        assertEquals(account1.getStatus(), StatusEnum.ACTIVE);
        assertEquals(account1.getNextStatus(), StatusEnum.ACTIVE);
        assertEquals(account1.getId(), approvedAccount.getId());
        assertEquals(account1.getIban(), approvedAccount.getIban());
        assertEquals(accountHistory1.getId(), createdAccountHistory.getId());
        assertEquals(accountHistory1.getAccountID(), createdAccountHistory.getAccountID());
        assertEquals(audit.getObjectID(), createdAudit.getObjectID());
        assertEquals(audit.getObjectType(), createdAudit.getObjectType());

        verify(accountRepository, times(1)).save(account1);
        verify(accountHistoryService, times(2)).saveAccountHistory(account1);
        verify(auditService, times(2)).saveAudit(audit);
    }

    @Test
    void testRejectAccount() throws Exception {
        audit.setOperation(OperationEnum.REJECT);
        account1.setStatus(StatusEnum.APPROVE);
        account1.setNextStatus(StatusEnum.ACTIVE);

        assertEquals(account1.getAccountStatus(), AccountStatusEnum.OPEN);

        when(accountRepository.findById(account1.getId())).thenReturn(Optional.ofNullable(account1));
        when(accountRepository.save(account1)).thenReturn(account1);
        when(accountHistoryService.saveAccountHistory(account1)).thenReturn(accountHistory1);
        when(auditService.saveAudit(audit)).thenReturn(audit);

        Account rejectedAccount = accountService.rejectAccount(account1.getId(), user.getId());
        AccountHistory createdAccountHistory = accountHistoryService.saveAccountHistory(account1);
        Audit createdAudit = auditService.saveAudit(audit);

        assertEquals(account1.getStatus(), StatusEnum.DELETE);
        assertEquals(account1.getNextStatus(), StatusEnum.DELETE);
        assertEquals(account1.getId(), rejectedAccount.getId());
        assertEquals(account1.getIban(), rejectedAccount.getIban());
        assertEquals(accountHistory1.getId(), createdAccountHistory.getId());
        assertEquals(accountHistory1.getAccountID(), createdAccountHistory.getAccountID());
        assertEquals(audit.getObjectID(), createdAudit.getObjectID());
        assertEquals(audit.getObjectType(), createdAudit.getObjectType());

        verify(accountRepository, times(1)).save(account1);
        verify(accountHistoryService, times(2)).saveAccountHistory(account1);
        verify(auditService, times(2)).saveAudit(audit);
    }
}
