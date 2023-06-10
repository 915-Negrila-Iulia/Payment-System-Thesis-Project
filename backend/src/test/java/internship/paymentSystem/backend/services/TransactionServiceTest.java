package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.client.Client;
import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.models.Account;
import internship.paymentSystem.backend.models.Person;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.User;
import internship.paymentSystem.backend.models.enums.*;
import internship.paymentSystem.backend.repositories.ITransactionRepository;
import internship.paymentSystem.backend.services.interfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @BeforeEach
    public void setup(){
        user = new User("iuli", "iulia.negrila06@gmail.com", "", RoleEnum.ADMIN_ROLE,
                StatusEnum.ACTIVE, StatusEnum.ACTIVE);
        user.setId(1L);

        person = new Person();
        person.setId(1L);
        person.setStatus(StatusEnum.ACTIVE);
        person.setNextStatus(StatusEnum.ACTIVE);
        person.setFirstName("Iuli");
        person.setLastName("NNN");
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
                BigDecimal.valueOf(100), senderAccount.getId(), receiverAccount.getId(),
                "FI43ABCD5375449011255431", "", "", StatusEnum.APPROVE, StatusEnum.ACTIVE);
        depositTransaction.setId(1L);
        withdrawalTransaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.WITHDRAWAL,
                BigDecimal.valueOf(100), senderAccount.getId(), receiverAccount.getId(),
                "FI43ABCD5375449011255431", "", "", StatusEnum.APPROVE, StatusEnum.ACTIVE);
        withdrawalTransaction.setId(2L);
        internalTransferTransaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.TRANSFER,
                BigDecimal.valueOf(100), senderAccount.getId(), receiverAccount.getId(),
                "FI43ABCD5375449011255431", "", "", StatusEnum.APPROVE, StatusEnum.ACTIVE);
        internalTransferTransaction.setId(3L);
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
    void testDepositTransaction(){

    }
}