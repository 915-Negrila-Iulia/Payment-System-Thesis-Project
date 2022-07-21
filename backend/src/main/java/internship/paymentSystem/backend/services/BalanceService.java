package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.Balance;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;
import internship.paymentSystem.backend.repositories.IBalanceRepository;
import internship.paymentSystem.backend.repositories.ITransactionRepository;
import internship.paymentSystem.backend.services.interfaces.IBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BalanceService implements IBalanceService {

    @Autowired
    IBalanceRepository balanceRepository;

    @Autowired
    ITransactionRepository transactionRepository;

    @Override
    public Balance saveBalance(Balance balance) {
        return balanceRepository.save(balance);
    }

    @Override
    public List<Balance> getAllBalances() {
        return balanceRepository.findAll();
    }

    @Override
    public List<Balance> getAllBalancesByAccountId(Long id){
        return balanceRepository.findAll().stream()
                .filter(balance -> Objects.equals(balance.getAccountID(), id))
                .collect(Collectors.toList());
    }

    @Override
    public Balance getCurrentBalance(Long id){
        return this.getAllBalancesByAccountId(id).stream()
                .reduce((previous, current) -> current)
                .orElse(null);
    }

    @Override
    public void updateAvailableAmount(Long accountId, Long transactionId){
        Transaction transaction = transactionRepository.findById(transactionId).get();
        Balance currentBalance = this.getCurrentBalance(accountId);
        Balance updatedBalance = new Balance(currentBalance.getTotal(), currentBalance.getAvailable(),
                currentBalance.getAccountID());
        //if transaction.getAction() == ActionTransactionEnum.DEPOSIT => do nothing
        if(transaction.getAction() == ActionTransactionEnum.WITHDRAWAL){
            updatedBalance.setAvailable(currentBalance.getAvailable() - transaction.getAmount());
        }
        else if(transaction.getAction() == ActionTransactionEnum.TRANSFER){
            updatedBalance.setAvailable(currentBalance.getAvailable() - transaction.getAmount());
            //in target account => do nothing
        }
        balanceRepository.save(updatedBalance);
    }

    @Override
    public void updateTotalAmount(Long transactionId){
        Transaction transaction = transactionRepository.findById(transactionId).get();
        Balance currentBalance = this.getCurrentBalance(transaction.getAccountID());
        Balance updatedBalance = new Balance(currentBalance.getTotal(), currentBalance.getAvailable(),
                currentBalance.getAccountID());
        if(transaction.getAction() == ActionTransactionEnum.DEPOSIT){
            updatedBalance.setAvailable(currentBalance.getAvailable() + transaction.getAmount());
            updatedBalance.setTotal(currentBalance.getTotal() + transaction.getAmount());
        }
        else if(transaction.getAction() == ActionTransactionEnum.WITHDRAWAL){
            updatedBalance.setTotal(currentBalance.getTotal() - transaction.getAmount());
        }
        else if(transaction.getAction() == ActionTransactionEnum.TRANSFER &&
                transaction.getType() == TypeTransactionEnum.INTERNAL) {
            updatedBalance.setTotal(currentBalance.getTotal() - transaction.getAmount());

            Balance targetAccountBalance = this.getCurrentBalance(transaction.getTargetAccountID());
            Balance updatedTargetAccountBalance = new Balance(targetAccountBalance.getTotal(),
                    targetAccountBalance.getAvailable(), targetAccountBalance.getAccountID());
            updatedTargetAccountBalance.setAvailable(targetAccountBalance.getAvailable() + transaction.getAmount());
            updatedTargetAccountBalance.setTotal(targetAccountBalance.getTotal() + transaction.getAmount());
            balanceRepository.save(updatedTargetAccountBalance);
        }
        else if(transaction.getAction() == ActionTransactionEnum.TRANSFER &&
                transaction.getType() == TypeTransactionEnum.EXTERNAL &&
                transaction.getStatus() == StatusEnum.ACTIVE){
            // if transaction type is EXTERNAL -> ips handles the balance of target account
            //                                 -> total balance of current account is changed only after ips approval
            updatedBalance.setTotal(currentBalance.getTotal() - transaction.getAmount());
        }

        balanceRepository.save(updatedBalance);
    }

    @Override
    public Balance createInitialBalance(Long accountId) {
        Balance initialBalance = new Balance(0D,0D,accountId);
        balanceRepository.save(initialBalance);
        return initialBalance;
    }

    @Override
    public void cancelAmountChanges(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).get();
        Balance currentBalance = this.getCurrentBalance(transaction.getAccountID());
        Balance updatedBalance = new Balance(currentBalance.getTotal(), currentBalance.getAvailable(),
                currentBalance.getAccountID());
        //if transaction.getAction() == ActionTransactionEnum.DEPOSIT => do nothing
        if(transaction.getAction() == ActionTransactionEnum.WITHDRAWAL){
            updatedBalance.setAvailable(currentBalance.getAvailable() + transaction.getAmount());
        }
        else if(transaction.getAction() == ActionTransactionEnum.TRANSFER){
            updatedBalance.setAvailable(currentBalance.getAvailable() + transaction.getAmount());
            //in target account => do nothing
        }
        balanceRepository.save(updatedBalance);
    }

}
