package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.models.Transaction;

import java.util.List;
import java.util.Optional;

public interface ITransactionService {

    Transaction saveTransaction(Transaction transaction);

    Optional<Transaction> findTransactionById(Long id);

    List<Transaction> getAllTransactions();

    Transaction depositTransaction(Transaction transactionDetails, Long currentUserId);

    Transaction withdrawalTransaction(Transaction transactionDetails, Long currentUserId);

    Transaction transferTransaction(Transaction transactionDetails, Long currentUserId);

    Transaction createTransaction(Transaction transaction, Long currentUserId);

    Transaction approveTransaction(Long id, Long currentUserId);

    Transaction rejectTransaction(Long id, Long currentUserId);

}
