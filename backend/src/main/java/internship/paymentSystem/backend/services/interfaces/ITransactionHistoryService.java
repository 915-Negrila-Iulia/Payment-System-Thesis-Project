package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.models.Person;
import internship.paymentSystem.backend.models.PersonHistory;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.TransactionHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface ITransactionHistoryService {

    TransactionHistory saveTransactionHistory(Transaction transaction);

    List<TransactionHistory> getHistoryOfTransactions();

    List<TransactionHistory> getHistoryByTransactionId(Long id);

    TransactionHistory getLastVersionOfTransaction(Long personId);

    List<TransactionHistory> getTransactionState(LocalDateTime timestamp);

}
