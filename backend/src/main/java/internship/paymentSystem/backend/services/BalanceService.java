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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /**
     * Available Amount
     * Check if the transaction exists by using the given id
     * Update available amount of Balance based on transaction type:
     *      - withdrawal => subtract amount given by transaction from available
     *      - transfer => subtract amount given by transaction from available
     * @param accountId id of the account that is updated
     * @param transactionId id of the transaction of account/s
     */
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

    /**
     * Total Amount
     * Check if the transaction exists by using the given id
     * Update total amount of Balance based on transaction type:
     *      - deposit => add amount given by transaction to available and total
     *      - withdrawal => subtract amount given by transaction from total
     *      - internal transfer => subtract amount given by transaction from total of account
     *                 => add amount given by transaction to available and total of target account
     *      - external transfer => subtract amount given by transaction from available of account
     *                          => than wait for authorization to modify the total
     * @param transactionId id of the transaction of account/s
     */
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
                transaction.getStatus() == StatusEnum.ACTIVE){
            // if transaction action is TRANSFER -> ips handles the balance of target account
            //                                 -> total balance of current account is changed only after ips approval
            updatedBalance.setTotal(currentBalance.getTotal() - transaction.getAmount());

            if(transaction.getType() == TypeTransactionEnum.INTERNAL){
                // if transaction type is INTERNAL -> total balance of current account and target account are changed
                Balance targetAccountBalance = this.getCurrentBalance(transaction.getTargetAccountID());
                Balance updatedTargetAccountBalance = new Balance(targetAccountBalance.getTotal(),
                        targetAccountBalance.getAvailable(), targetAccountBalance.getAccountID());
                updatedTargetAccountBalance.setAvailable(targetAccountBalance.getAvailable() + transaction.getAmount());
                updatedTargetAccountBalance.setTotal(targetAccountBalance.getTotal() + transaction.getAmount());
                balanceRepository.save(updatedTargetAccountBalance);
            }
        }

        balanceRepository.save(updatedBalance);
    }

    @Override
    public Balance createInitialBalance(Long accountId) {
        Balance initialBalance = new Balance(0D,0D,accountId);
        balanceRepository.save(initialBalance);
        return initialBalance;
    }

    /**
     * Available Amount
     * Check if the transaction exists by using the given id
     * Update available amount of Balance based on transaction type:
     *      - withdrawal => add (restore) amount given by transaction to available
     *      - transfer => add (restore) amount given by transaction to available
     * @param transactionId id of the transaction of account/s
     */
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

    @Override
    public List<Balance> filterByDates(String startDate, String endDate) {
        String startDateString = startDate + " 00:00:00";
        String endDateString = endDate + " 23:59:59";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(startDateString, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDateString, formatter);
        return balanceRepository.findAll().stream()
                .filter(balance -> !(balance.getTimestamp().isBefore(startDateTime)
                                    || balance.getTimestamp().isAfter(endDateTime)))
                .collect(Collectors.toList());
    }

}
