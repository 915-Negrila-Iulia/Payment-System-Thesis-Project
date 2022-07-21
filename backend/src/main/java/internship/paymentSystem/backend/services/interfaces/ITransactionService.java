package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.models.Transaction;

import java.util.List;
import java.util.Optional;

public interface ITransactionService {

    Transaction saveTransaction(Transaction transaction);

    Optional<Transaction> findTransactionById(Long id);

    List<Transaction> getAllTransactions();

    Transaction depositTransaction(Transaction transactionDetails, Long currentUserId) throws Exception;

    Transaction withdrawalTransaction(Transaction transactionDetails, Long currentUserId) throws Exception;

    Transaction transferTransaction(Transaction transactionDetails, Long currentUserId) throws Exception;

    Transaction createTransaction(Transaction transaction, Long currentUserId);

    Transaction approveTransaction(Long id, Long currentUserId) throws Exception;

    Transaction rejectTransaction(Long id, Long currentUserId) throws Exception;

    Transaction authorizeTransaction(Long id) throws Exception;

    Transaction notAuthorizeTransaction(Long id) throws Exception;
}
