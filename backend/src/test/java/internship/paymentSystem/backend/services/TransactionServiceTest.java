package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.DTOs.StatisticDto;
import internship.paymentSystem.backend.DTOs.TransactionBuilderContext;
import internship.paymentSystem.backend.client.Client;
import internship.paymentSystem.backend.customExceptions.FraudException;
import internship.paymentSystem.backend.models.*;
import internship.paymentSystem.backend.models.enums.*;
import internship.paymentSystem.backend.repositories.ITransactionRepository;
import internship.paymentSystem.backend.services.interfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private ITransactionRepository transactionRepository;
    @Mock
    private ITransactionHistoryService transactionHistoryService;
    @Mock
    private IBalanceService balanceService;
    @Mock
    private IUserService userService;
    @Mock
    private IPersonService personService;
    @Mock
    private IAccountService accountService;
    @Mock
    private IAuditService auditService;
    @Mock
    private Client appClient;
    @Mock
    private IEmailService emailService;
    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Person person;
    private Account senderAccount, receiverAccount;
    private Transaction depositTransaction, withdrawalTransaction, internalTransferTransaction;
    private TransactionBuilderContext context;

    @BeforeEach
    public void setup(){
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
        person.setDateOfBirth(new Date(1997,10,10));
        person.setPhoneNumber("0745698412");
        person.setUserID(user.getId());

        senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setStatus(StatusEnum.ACTIVE);
        senderAccount.setNextStatus(StatusEnum.ACTIVE);
        senderAccount.setAccountStatus(AccountStatusEnum.OPEN);
        senderAccount.setIban("RO58BTRL8430952977069558");
        senderAccount.setBankCode("BTRL");
        senderAccount.setCountryCode("RO");
        senderAccount.setCurrency("EUR");
        senderAccount.setPersonID(person.getId());

        receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverAccount.setStatus(StatusEnum.ACTIVE);
        receiverAccount.setNextStatus(StatusEnum.ACTIVE);
        receiverAccount.setAccountStatus(AccountStatusEnum.OPEN);
        receiverAccount.setIban("FI43ABCD5375449011255431");
        receiverAccount.setBankCode("ABCD");
        receiverAccount.setCountryCode("FI");
        receiverAccount.setCurrency("EUR");
        receiverAccount.setPersonID(person.getId());

        depositTransaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.DEPOSIT,
                BigDecimal.valueOf(100), senderAccount.getId(), null,
                null, null, null, StatusEnum.APPROVE, StatusEnum.ACTIVE);
        depositTransaction.setId(1L);
        withdrawalTransaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.WITHDRAWAL,
                BigDecimal.valueOf(100), senderAccount.getId(), null,
                null, null, null, StatusEnum.APPROVE, StatusEnum.ACTIVE);
        withdrawalTransaction.setId(2L);
        internalTransferTransaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.TRANSFER,
                BigDecimal.valueOf(100), senderAccount.getId(), receiverAccount.getId(),
                "FI43ABCD5375449011255431", "", "", StatusEnum.APPROVE, StatusEnum.ACTIVE);
        internalTransferTransaction.setId(3L);

        // remain to be set: transactionDetails, accountId
        context = new TransactionBuilderContext();
        context.setCurrentUserId(user.getId());
        context.setAccountUserId(user.getId());
        context.setAccountStatus(AccountStatusEnum.OPEN);
        context.setTargetAccountStatus(AccountStatusEnum.OPEN);
        context.setTransactionRepository(transactionRepository);
        context.setTransactionHistoryService(transactionHistoryService);
        context.setBalanceService(balanceService);
        context.setAuditService(auditService);
        context.setAppClient(appClient);
        context.setCurrentUserEmail(user.getEmail());
        context.setEmailService(emailService);

    }

    @Test
    void testGetAllTransactions(){
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(depositTransaction);
        transactions.add(withdrawalTransaction);

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> allTransactions = transactionService.getAllTransactions();

        assertEquals(transactions, allTransactions);
        assertEquals(allTransactions.size(), 2);

        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionsByStatus() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(depositTransaction);
        withdrawalTransaction.setStatus(StatusEnum.FRAUD);
        transactions.add(withdrawalTransaction);
        internalTransferTransaction.setStatus(StatusEnum.FRAUD);
        transactions.add(internalTransferTransaction);

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> filteredTransactions = transactionService.getTransactionsByStatus(StatusEnum.FRAUD);

        assertEquals(filteredTransactions.size(), 2);
        assertEquals(filteredTransactions.get(0).getStatus(), StatusEnum.FRAUD);

        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionsByAccountId() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(depositTransaction);
        transactions.add(withdrawalTransaction);
        internalTransferTransaction.setAccountID(9999L);
        transactions.add(internalTransferTransaction);

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> filteredTransactions = transactionService.getTransactionsByAccountId(senderAccount.getId());

        assertEquals(filteredTransactions.size(), 2);
        assertEquals(filteredTransactions.get(0).getAccountID(), senderAccount.getId());

        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testTransactionsOfUser() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(depositTransaction);
        transactions.add(withdrawalTransaction);

        List<Account> accounts = new ArrayList<>();
        accounts.add(senderAccount);
        accounts.add(receiverAccount);

        when(transactionRepository.findAll()).thenReturn(transactions);
        when(accountService.getAccountsOfUser(user.getId())).thenReturn(accounts);

        List<Transaction> filteredTransactions = transactionService.getTransactionsOfUser(user.getId());

        assertEquals(filteredTransactions.size(), 2);

        verify(transactionRepository, times(1)).findAll();
        verify(accountService, times(1)).getAccountsOfUser(user.getId());
    }

    @Test
    void testDepositTransaction() throws Exception {
        Long userId = user.getId();

        when(personService.findPersonById(any())).thenReturn(Optional.ofNullable(person));
        when(accountService.findAccountById(any())).thenReturn(Optional.ofNullable(senderAccount));
        when(userService.findUserById(userId)).thenReturn(Optional.ofNullable(user));

        Transaction finalTransaction = transactionService.depositTransaction(depositTransaction,userId);

        assertEquals(finalTransaction.getStatus(), depositTransaction.getStatus());
        assertEquals(finalTransaction.getNextStatus(), depositTransaction.getNextStatus());
        assertEquals(finalTransaction.getAction(), depositTransaction.getAction());
        assertEquals(finalTransaction.getAction(), ActionTransactionEnum.DEPOSIT);
        assertEquals(finalTransaction.getAmount(), depositTransaction.getAmount());
        assertEquals(finalTransaction.getAccountID(), depositTransaction.getAccountID());
        assertEquals(finalTransaction.getTargetAccountID(), depositTransaction.getTargetAccountID());

        verify(userService, times(2)).findUserById(userId);
        verify(personService, times(2)).findPersonById(any());
        verify(accountService, times(4)).findAccountById(any());
    }

    @Test
    void testWithdrawalTransaction() throws Exception {
        Long userId = user.getId();
        Balance balance = new Balance(BigDecimal.valueOf(100), BigDecimal.valueOf(100), senderAccount.getId());
        List<Object> fraudCheckList = Arrays.asList(18.25F,"notFraud");

        when(personService.findPersonById(any())).thenReturn(Optional.ofNullable(person));
        when(accountService.findAccountById(any())).thenReturn(Optional.ofNullable(senderAccount));
        when(userService.findUserById(userId)).thenReturn(Optional.ofNullable(user));
        when(balanceService.getCurrentBalance(any())).thenReturn(balance);
        when(appClient.isFraudCheck(anyInt(), any(), any(), any(), any(), any(), any()))
                .thenReturn(fraudCheckList);

        Transaction finalTransaction = transactionService.withdrawalTransaction(withdrawalTransaction,userId);

        assertEquals(finalTransaction.getStatus(), withdrawalTransaction.getStatus());
        assertEquals(finalTransaction.getNextStatus(), withdrawalTransaction.getNextStatus());
        assertEquals(finalTransaction.getAction(), withdrawalTransaction.getAction());
        assertEquals(finalTransaction.getAction(), ActionTransactionEnum.WITHDRAWAL);
        assertEquals(finalTransaction.getAmount(), withdrawalTransaction.getAmount());
        assertEquals(finalTransaction.getAccountID(), withdrawalTransaction.getAccountID());
        assertEquals(finalTransaction.getTargetAccountID(), withdrawalTransaction.getTargetAccountID());

        verify(userService, times(2)).findUserById(userId);
        verify(personService, times(2)).findPersonById(any());
        verify(accountService, times(4)).findAccountById(any());
    }

    @Test
    void testWithdrawalTransactionIsFraud() throws Exception {
        Long userId = user.getId();
        Balance balance = new Balance(BigDecimal.valueOf(100), BigDecimal.valueOf(100), senderAccount.getId());
        Audit audit = new Audit(withdrawalTransaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE, userId);
        audit.setTimestamp(LocalDateTime.now());
        List<Object> fraudCheckList = Arrays.asList(98.25F,"Fraud");

        when(personService.findPersonById(any())).thenReturn(Optional.ofNullable(person));
        when(accountService.findAccountById(any())).thenReturn(Optional.ofNullable(senderAccount));
        when(userService.findUserById(userId)).thenReturn(Optional.ofNullable(user));
        when(balanceService.getCurrentBalance(any())).thenReturn(balance);
        when(auditService.saveAudit(any())).thenReturn(audit);
        when(appClient.isFraudCheck(anyInt(), any(), any(), any(), any(), any(), any()))
                .thenReturn(fraudCheckList);

        try {
            Transaction finalTransaction = transactionService.withdrawalTransaction(withdrawalTransaction, userId);

        } catch (FraudException fe) {
            assertEquals("Transaction suspected as fraud. Please check your email for further details.",
                    fe.getMessage());
            assertEquals(98.25F, fe.getFraudProbability());
        }

        verify(userService, times(2)).findUserById(userId);
        verify(personService, times(2)).findPersonById(any());
        verify(accountService, times(4)).findAccountById(any());
    }

    @Test
    void testInternalTransfer() throws Exception {
        Long userId = user.getId();
        Balance balance = new Balance(BigDecimal.valueOf(100), BigDecimal.valueOf(100), senderAccount.getId());
        List<Object> fraudCheckList = Arrays.asList(18.25F,"notFraud");

        when(personService.findPersonById(any())).thenReturn(Optional.ofNullable(person));
        when(accountService.findAccountById(any())).thenReturn(Optional.ofNullable(senderAccount));
        when(userService.findUserById(userId)).thenReturn(Optional.ofNullable(user));
        when(balanceService.getCurrentBalance(any())).thenReturn(balance);
        when(appClient.isFraudCheck(anyInt(), any(), any(), any(), any(), any(), any()))
                .thenReturn(fraudCheckList);

        Transaction finalTransaction = transactionService.transferTransaction(internalTransferTransaction,userId);

        assertEquals(finalTransaction.getStatus(), internalTransferTransaction.getStatus());
        assertEquals(finalTransaction.getNextStatus(), internalTransferTransaction.getNextStatus());
        assertEquals(finalTransaction.getAction(), internalTransferTransaction.getAction());
        assertEquals(finalTransaction.getAction(), ActionTransactionEnum.TRANSFER);
        assertEquals(finalTransaction.getAmount(), internalTransferTransaction.getAmount());
        assertEquals(finalTransaction.getAccountID(), internalTransferTransaction.getAccountID());
        assertEquals(finalTransaction.getTargetAccountID(), internalTransferTransaction.getTargetAccountID());

        verify(userService, times(2)).findUserById(userId);
        verify(personService, times(2)).findPersonById(any());
        verify(accountService, times(6)).findAccountById(any());
    }

    @Test
    void testInternalTransferIsFraud() throws Exception {
        Long userId = user.getId();
        Balance balance = new Balance(BigDecimal.valueOf(100), BigDecimal.valueOf(100), senderAccount.getId());
        Audit audit = new Audit(internalTransferTransaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE, userId);
        audit.setTimestamp(LocalDateTime.now());
        List<Object> fraudCheckList = Arrays.asList(98.25F,"Fraud");

        when(personService.findPersonById(any())).thenReturn(Optional.ofNullable(person));
        when(accountService.findAccountById(any())).thenReturn(Optional.ofNullable(senderAccount));
        when(userService.findUserById(userId)).thenReturn(Optional.ofNullable(user));
        when(balanceService.getCurrentBalance(any())).thenReturn(balance);
        when(auditService.saveAudit(any())).thenReturn(audit);
        when(appClient.isFraudCheck(anyInt(), any(), any(), any(), any(), any(), any()))
                .thenReturn(fraudCheckList);

        try {
            Transaction finalTransaction = transactionService.internalTransfer(internalTransferTransaction, userId);

        } catch (FraudException fe) {
            assertEquals("Transaction suspected as fraud. Please check your email for further details.",
                    fe.getMessage());
            assertEquals(98.25F, fe.getFraudProbability());
        }

        verify(userService, times(2)).findUserById(userId);
        verify(personService, times(2)).findPersonById(any());
        verify(accountService, times(6)).findAccountById(any());
    }

    @Test
    void testConfirmSuspectTransaction() throws Exception {
        String reference = UUID.randomUUID().toString().replaceAll("-","");
        withdrawalTransaction.setReference(reference);
        withdrawalTransaction.setStatus(StatusEnum.SUSPECTED_FRAUD);
        withdrawalTransaction.setNextStatus(StatusEnum.FRAUD);

        assertEquals(withdrawalTransaction.getStatus(), StatusEnum.SUSPECTED_FRAUD);
        assertEquals(withdrawalTransaction.getNextStatus(), StatusEnum.FRAUD);

        when(transactionRepository.findByReference(any())).thenReturn(withdrawalTransaction);

        Transaction confirmedTransaction = transactionService.confirmSuspectTransaction(withdrawalTransaction.getReference());

        assertEquals(confirmedTransaction.getStatus(), withdrawalTransaction.getStatus());
        assertEquals(confirmedTransaction.getStatus(), StatusEnum.APPROVE);
        assertEquals(confirmedTransaction.getNextStatus(), withdrawalTransaction.getNextStatus());
        assertEquals(confirmedTransaction.getNextStatus(), StatusEnum.ACTIVE);
        assertEquals(confirmedTransaction.getAction(), withdrawalTransaction.getAction());
        assertEquals(confirmedTransaction.getAmount(), withdrawalTransaction.getAmount());
        assertEquals(confirmedTransaction.getAccountID(), withdrawalTransaction.getAccountID());
        assertEquals(confirmedTransaction.getTargetAccountID(), withdrawalTransaction.getTargetAccountID());

        verify(transactionRepository, times(1)).findByReference(withdrawalTransaction.getReference());
        verify(transactionHistoryService, times(1)).saveTransactionHistory(any());
        verify(balanceService, times(1)).updateAvailableAmount(senderAccount.getId(),
                withdrawalTransaction.getId());
        verify(transactionRepository, times(1)).save(withdrawalTransaction);
        verify(auditService, times(1)).saveAudit(any());
    }

    @Test
    void testGetCountFraudsInSystem(){
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(depositTransaction);
        transactions.add(withdrawalTransaction);
        internalTransferTransaction.setStatus(StatusEnum.FRAUD);
        internalTransferTransaction.setNextStatus(StatusEnum.FRAUD);
        transactions.add(internalTransferTransaction);

        when(transactionService.getAllTransactions()).thenReturn(transactions);

        Long countFrauds = transactionService.getCountFraudsInSystem();

        assertEquals(1, countFrauds);
    }

    @Test
    void testGetStatisticsOfAccount(){
        depositTransaction.setStatus(StatusEnum.APPROVE);
        withdrawalTransaction.setStatus(StatusEnum.ACTIVE);
        internalTransferTransaction.setStatus(StatusEnum.ACTIVE);
        Transaction deletedTransaction = new Transaction();
        deletedTransaction.setAccountID(senderAccount.getId());
        deletedTransaction.setStatus(StatusEnum.DELETE);
        deletedTransaction.setAmount(BigDecimal.TEN);

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(depositTransaction,withdrawalTransaction,
                internalTransferTransaction, deletedTransaction));

        List<StatisticDto> statistics = transactionService.getStatisticsOfAccount(senderAccount.getId());

        assertEquals(statistics.get(0).getStatus(), StatusEnum.APPROVE);
        assertEquals(statistics.get(0).getCount(), 1L);
        assertEquals(statistics.get(0).getAmount(), BigDecimal.valueOf(100));

        assertEquals(statistics.get(1).getStatus(), StatusEnum.AUTHORIZE);
        assertEquals(statistics.get(1).getCount(), 0L);
        assertEquals(statistics.get(1).getAmount(), BigDecimal.ZERO);

        assertEquals(statistics.get(2).getStatus(), StatusEnum.ACTIVE);
        assertEquals(statistics.get(2).getCount(), 2L);
        assertEquals(statistics.get(2).getAmount(), BigDecimal.valueOf(200));

        assertEquals(statistics.get(3).getStatus(), StatusEnum.DELETE);
        assertEquals(statistics.get(3).getCount(), 1L);
        assertEquals(statistics.get(3).getAmount(), BigDecimal.TEN);
    }

}