package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.TransactionHistory;
import internship.paymentSystem.backend.repositories.ITransactionHistoryRepository;
import internship.paymentSystem.backend.services.interfaces.ITransactionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransactionHistoryService implements ITransactionHistoryService {

    @Autowired
    ITransactionHistoryRepository transactionHistoryRepository;

    @Override
    public TransactionHistory saveTransactionHistory(Transaction transaction) {
        TransactionHistory transactionHistory = new TransactionHistory(transaction.getType(), transaction.getAction(),
                transaction.getAmount(), transaction.getAccountID(), transaction.getStatus(), transaction.getNextStatus(),
                transaction.getId());
        if(transaction.getTargetAccountID() != null) {
            transactionHistory.setTargetAccountID(transaction.getTargetAccountID());
        }
        return transactionHistoryRepository.save(transactionHistory);
    }

    @Override
    public List<TransactionHistory> getHistoryOfTransactions() {
        return transactionHistoryRepository.findAll();
    }

    @Override
    public List<TransactionHistory> getHistoryByTransactionId(Long id) {
        return transactionHistoryRepository.findAll().stream()
                .filter(transaction -> Objects.equals(transaction.getTransactionID(), id))
                .collect(Collectors.toList());
    }

    @Override
    public TransactionHistory getLastVersionOfTransaction(Long personId) {
        List<TransactionHistory> history = this.getHistoryByTransactionId(personId);
        TransactionHistory findLastVersion = history.stream().max(Comparator.comparing(TransactionHistory::getTimestamp)).get();
        TransactionHistory lastVersion = new TransactionHistory(findLastVersion.getType(), findLastVersion.getAction(),
                findLastVersion.getAmount(), findLastVersion.getAccountID(), findLastVersion.getStatus(),
                findLastVersion.getNextStatus());
        return lastVersion;
    }

    @Override
    public List<TransactionHistory> getTransactionState(LocalDateTime timestamp) {
        return transactionHistoryRepository.findAll().stream()
                .filter(transaction -> transaction.getHistoryTimestamp().isEqual(timestamp)).collect(Collectors.toList());
    }

}
