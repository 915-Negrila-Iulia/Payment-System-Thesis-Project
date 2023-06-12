package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.Balance;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.*;
import internship.paymentSystem.backend.repositories.IBalanceRepository;
import internship.paymentSystem.backend.repositories.ITransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @Mock
    IBalanceRepository balanceRepository;
    @Mock
    ITransactionRepository transactionRepository;
    @InjectMocks
    private BalanceService balanceService;

    private Balance balance1, balance2, balance3;

    @BeforeEach
    public void setup(){
        balance1 = new Balance(BigDecimal.valueOf(190), BigDecimal.valueOf(150), 1L);
        balance1.setId(1L);
        balance2 = new Balance(BigDecimal.ZERO, BigDecimal.ZERO, 2L);
        balance2.setId(1L);
        balance3 = new Balance(BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), 2L);
        balance3.setId(2L);
    }

    @Test
    void testGetAllBalances(){
        List<Balance> balances = new ArrayList<>();
        balances.add(balance1);
        balances.add(balance2);
        balances.add(balance3);

        when(balanceRepository.findAll()).thenReturn(balances);

        List<Balance> allBalances = balanceService.getAllBalances();

        assertEquals(balances, allBalances);
        assertEquals(allBalances.size(), 3);

        verify(balanceRepository, times(1)).findAll();
    }

    @Test
    void testGetAllBalancesByAccountId(){
        List<Balance> balances = new ArrayList<>();
        balances.add(balance1);
        balances.add(balance2);
        balances.add(balance3);

        when(balanceRepository.findAll()).thenReturn(balances);

        List<Balance> allBalancesByAccountId = balanceService.getAllBalancesByAccountId(2L);

        assertEquals(allBalancesByAccountId.get(0).getAccountID(), 2L);
        assertEquals(allBalancesByAccountId.size(), 2);

        verify(balanceRepository, times(1)).findAll();
    }

    @Test
    void testGetCurrentBalance(){
        List<Balance> balances = new ArrayList<>();
        balances.add(balance1);
        balances.add(balance2);
        balances.add(balance3);

        when(balanceRepository.findAll()).thenReturn(balances);

        Balance currentBalance = balanceService.getCurrentBalance(2L);

        assertEquals(currentBalance.getAccountID(), 2L);
        assertEquals(currentBalance.getTotal(), BigDecimal.valueOf(1000));
        assertEquals(currentBalance.getId(), 2L);

        verify(balanceRepository, times(1)).findAll();
    }

    @Test
    void testUpdateTotalAmount(){
        Long accountId = 2L;
        Transaction depositTransaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.DEPOSIT,
                BigDecimal.valueOf(100), accountId, null,
                null, null, null, StatusEnum.APPROVE, StatusEnum.ACTIVE);
        depositTransaction.setId(1L);
        List<Balance> balances = new ArrayList<>();
        balances.add(balance1);
        balances.add(balance2);
        balances.add(balance3);
        Balance updatedBalance = new Balance(BigDecimal.valueOf(1100), BigDecimal.valueOf(1100), accountId);

        when(transactionRepository.findById(depositTransaction.getId())).thenReturn(Optional.of(depositTransaction));
        when(balanceRepository.findAll()).thenReturn(balances);
        when(balanceRepository.save(updatedBalance)).thenReturn(updatedBalance);

        balanceService.updateTotalAmount(depositTransaction.getId());

        verify(transactionRepository, times(1)).findById(depositTransaction.getId());
        verify(balanceRepository, times(1)).findAll();
        verify(balanceRepository, times(1)).save(argThat(balance ->
                        Objects.equals(balance.getTotal(), updatedBalance.getTotal()) &&
                        Objects.equals(balance.getAvailable(), updatedBalance.getAvailable()) &&
                        Objects.equals(balance.getAccountID(), updatedBalance.getAccountID())
        ));
    }
}
