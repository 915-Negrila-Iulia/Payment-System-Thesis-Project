package com.example.backend.services;

import com.example.backend.models.Audit;
import com.example.backend.models.bases.BaseEntity;
import com.example.backend.models.enumerations.*;
import com.example.backend.models.Transaction;
import com.example.backend.repositories.ITransactionRepository;
import com.example.backend.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private IBalanceService balanceService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IAuditService auditService;

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    private Long getUserIdOfAccount(Long accountId){
        Long personId = accountService.findAccountById(accountId).get().getPersonID();
        Long userId = personService.findPersonById(personId).get().getUserID();
        return userId;
    }

    /**
     * Deposit amount of money
     * Check if user that wants to deposit is the owner of the account
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * Update balance of the account that initiated the transaction
     * @param transactionDetails details of deposit transaction
     * @param currentUserId id of user performing the transaction
     * @return created transaction
     */
    @Override
    public Transaction depositTransaction(Transaction transactionDetails, Long currentUserId){
        Long accountId = transactionDetails.getAccountID();
        Long userId = this.getUserIdOfAccount(accountId);
        if(Objects.equals(userId, currentUserId)) {
            Double amount = transactionDetails.getAmount();
            Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.DEPOSIT, amount,
                    accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
            transactionRepository.save(transaction);
            balanceService.updateAvailableAmount(accountId, transaction.getId());
            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE, currentUserId);
            auditService.saveAudit(audit);
            return transaction;
        }
        return null;
    }

    /**
     * Withdrawal amount of money
     * Check if user that wants to withdrawal is the owner of the account
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * Update balance of the account that initiated the transaction
     * @param transactionDetails details of withdrawal transaction
     * @param currentUserId id of user performing the transaction
     * @return created transaction
     */
    @Override
    public Transaction withdrawalTransaction(Transaction transactionDetails, Long currentUserId) {
        Long accountId = transactionDetails.getAccountID();
        Long userId = this.getUserIdOfAccount(accountId);
        if(Objects.equals(userId, currentUserId)) {
            Double amount = transactionDetails.getAmount();
            Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.WITHDRAWAL, amount,
                    accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
            transactionRepository.save(transaction);
            balanceService.updateAvailableAmount(accountId, transaction.getId());
            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE, currentUserId);
            auditService.saveAudit(audit);
            return transaction;
        }
        return null;
    }

    /**
     * Transfer amount of money between accounts
     * Check if user that wants to transfer is the owner of the account
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * Update balance of the account that initiated the transaction
     * @param transactionDetails details of transfer transaction
     * @param currentUserId id of user performing the transaction
     * @return created transaction
     */
    @Override
    public Transaction transferTransaction(Transaction transactionDetails, Long currentUserId) {
        Long accountId = transactionDetails.getAccountID();
        Long userId = this.getUserIdOfAccount(accountId);
        if(Objects.equals(userId, currentUserId)) {
            Long targetId = transactionDetails.getTargetAccountID();
            Double amount = transactionDetails.getAmount();
            Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.TRANSFER, amount,
                    accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
            transaction.setTargetAccountID(targetId);
            transactionRepository.save(transaction);
            balanceService.updateAvailableAmount(accountId, transaction.getId());
            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE, currentUserId);
            auditService.saveAudit(audit);
            return transaction;
        }
        return null;
    }

    @Override
    public Transaction createTransaction(Transaction transactionDetails, Long currentUserId) {
        transactionDetails.setStatus(StatusEnum.APPROVE);
        transactionDetails.setNextStatus(StatusEnum.ACTIVE);
        Transaction transaction = transactionRepository.save(transactionDetails);
        Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
        return transaction;
    }

    /**
     * Approve transaction
     * Check if the transaction exists by using the given id and throw an exception otherwise
     * Check if user that wants to approve transaction is not the same one that made it previously
     * Change 'Status' to 'NextStatus'
     * Update 'Audit' table
     * Update balance of the account that initiated the transaction
     * @param id of the transaction that is approved
     * @param currentUserId id of user performing the approval
     * @return the approved transaction
     */
    @Override
    public Transaction approveTransaction(Long id, Long currentUserId) {
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.TRANSACTION), currentUserId)) {
            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found"));
            transaction.setStatus(transaction.getNextStatus());
            Transaction activeTransaction = transactionRepository.save(transaction);
            balanceService.updateTotalAmount(transaction.getId());
            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.APPROVE, currentUserId);
            auditService.saveAudit(audit);
            return activeTransaction;
        }
        return null;
    }

    /**
     * Reject/Delete transaction
     * Check if the transaction exists by using the given id and throw an exception otherwise
     * Check if user that wants to reject transaction is not the same one that made it previously
     * Change 'Status' to 'DELETE' and 'NextStatus' to 'DELETE'
     * Update 'Audit' table
     * Update balance of the account that initiated the transaction
     * @param id of the transaction that is rejected
     * @param currentUserId id of user performing the rejection
     * @return the rejected transaction
     */
    @Override
    public Transaction rejectTransaction(Long id, Long currentUserId) {
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.TRANSACTION), currentUserId)) {
            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found"));
            transaction.setStatus(StatusEnum.DELETE);
            transaction.setNextStatus(StatusEnum.DELETE);
            Transaction rejectedTransaction = transactionRepository.save(transaction);
            balanceService.cancelAmountChanges(transaction.getId());
            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.REJECT, currentUserId);
            auditService.saveAudit(audit);
            return rejectedTransaction;
        }
        return null;
    }

}
