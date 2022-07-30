package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.DTOs.StatisticDto;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.TransactionHistory;
import internship.paymentSystem.backend.models.enums.StatusEnum;

import java.util.List;
import java.util.Optional;

public interface ITransactionService {

    Transaction saveTransaction(Transaction transaction);

    Optional<Transaction> findTransactionById(Long id);

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByStatus(StatusEnum filterStatus);

    List<Transaction> getTransactionsByAccountId(Long id);

    Transaction depositTransaction(Transaction transactionDetails, Long currentUserId) throws Exception;

    Transaction withdrawalTransaction(Transaction transactionDetails, Long currentUserId) throws Exception;

    Transaction transferTransaction(Transaction transactionDetails, Long currentUserId) throws Exception;

    Transaction createTransaction(Transaction transaction, Long currentUserId);

    Transaction approveTransaction(Long id, Long currentUserId) throws Exception;

    Transaction rejectTransaction(Long id, Long currentUserId) throws Exception;

    Transaction authorizeTransaction(Long id) throws Exception;

    Transaction notAuthorizeTransaction(Long id) throws Exception;

    List<TransactionHistory> getHistoryByTransactionId(Long transactionId);

    List<TransactionHistory> getHistoryOfTransactions();

    List<StatisticDto> getStatisticsOfAccount(Long accountId);
}
