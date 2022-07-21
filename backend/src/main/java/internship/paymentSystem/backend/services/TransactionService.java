package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.*;
import internship.paymentSystem.backend.repositories.ITransactionRepository;
import internship.paymentSystem.backend.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private AccountStatusEnum getStatusByAccountId(Long accountId){
        return accountService.findAccountById(accountId).get().getAccountStatus();
    }

    private String getCurrencyByAccountId(Long accountId){
        return accountService.findAccountById(accountId).get().getCurrency();
    }

    /**
     * Deposit amount of money
     * Check if user that wants to deposit is the owner of the account
     * Throw exception if credit is blocked
     * Throw exception if user logged is not the owner of the account
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * Update balance of the account that initiated the transaction
     * @param transactionDetails details of deposit transaction
     * @param currentUserId id of user performing the transaction
     * @return created transaction
     */
    @Transactional
    @Override
    public Transaction depositTransaction(Transaction transactionDetails, Long currentUserId) throws Exception {
        Long accountId = transactionDetails.getAccountID();
        Long userId = this.getUserIdOfAccount(accountId);
        if(Objects.equals(userId, currentUserId)) {
            if(!(Objects.equals(getStatusByAccountId(accountId),AccountStatusEnum.BLOCK_CREDIT) ||
                    Objects.equals(getStatusByAccountId(accountId),AccountStatusEnum.BLOCKED))){
                Double amount = transactionDetails.getAmount();
                Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.DEPOSIT, amount,
                        accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
                transactionRepository.save(transaction);
                balanceService.updateAvailableAmount(accountId, transaction.getId());
                Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE, currentUserId);
                auditService.saveAudit(audit);
                return transaction;
            }
            else{
                throw new Exception("Credit Blocked. Not allowed to deposit");
            }
        }
        else{
            throw new Exception("Not allowed to make transaction");
        }
    }

    /**
     * Withdrawal amount of money
     * Check if user that wants to withdrawal is the owner of the account
     * Throw exception if debit is blocked
     * Throw exception if user logged is not the owner of the account
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * Update balance of the account that initiated the transaction
     * @param transactionDetails details of withdrawal transaction
     * @param currentUserId id of user performing the transaction
     * @return created transaction
     */
    @Transactional
    @Override
    public Transaction withdrawalTransaction(Transaction transactionDetails, Long currentUserId) throws Exception {
        Long accountId = transactionDetails.getAccountID();
        Long userId = this.getUserIdOfAccount(accountId);
        if(Objects.equals(userId, currentUserId)) {
            if(!(Objects.equals(getStatusByAccountId(accountId),AccountStatusEnum.BLOCK_DEBIT) ||
                    Objects.equals(getStatusByAccountId(accountId),AccountStatusEnum.BLOCKED))) {
                Double amount = transactionDetails.getAmount();
                Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.WITHDRAWAL, amount,
                        accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
                transactionRepository.save(transaction);
                balanceService.updateAvailableAmount(accountId, transaction.getId());
                Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE, currentUserId);
                auditService.saveAudit(audit);
                return transaction;
            }
            else{
                throw new Exception("Debit Blocked. Not allowed to withdraw");
            }
        }
        else{
            throw new Exception("Not allowed to make transaction");
        }
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
    @Transactional
    @Override
    public Transaction transferTransaction(Transaction transactionDetails, Long currentUserId) throws Exception {
        Long accountId = transactionDetails.getAccountID();
        Long userId = this.getUserIdOfAccount(accountId);
        Long targetId = transactionDetails.getTargetAccountID();
        if(Objects.equals(userId, currentUserId)) {
            if(!(Objects.equals(getStatusByAccountId(accountId),AccountStatusEnum.BLOCK_CREDIT) ||
                    Objects.equals(getStatusByAccountId(accountId),AccountStatusEnum.BLOCKED))) {
                if(!(Objects.equals(getStatusByAccountId(targetId),AccountStatusEnum.BLOCK_DEBIT) ||
                        Objects.equals(getStatusByAccountId(targetId),AccountStatusEnum.BLOCKED))) {
                    if(Objects.equals(getCurrencyByAccountId(accountId),getCurrencyByAccountId(targetId))) {
                        if(transactionDetails.getType() == TypeTransactionEnum.INTERNAL) {
                            Double amount = transactionDetails.getAmount();
                            Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL,
                                    ActionTransactionEnum.TRANSFER, amount, accountId,
                                    StatusEnum.APPROVE, StatusEnum.ACTIVE);
                            transaction.setTargetAccountID(targetId);
                            transactionRepository.save(transaction);
                            balanceService.updateAvailableAmount(accountId, transaction.getId());
                            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION,
                                    OperationEnum.CREATE, currentUserId);
                            auditService.saveAudit(audit);
                            return transaction;
                        }
                        else{
                            // ips transfer
                            Double amount = transactionDetails.getAmount();
                            Transaction transaction = new Transaction(TypeTransactionEnum.EXTERNAL,
                                    ActionTransactionEnum.TRANSFER, amount, accountId,
                                    StatusEnum.APPROVE, StatusEnum.AUTHORIZE);
                            transaction.setTargetAccountID(targetId);  // change to target iban - external
                            transactionRepository.save(transaction);
                            balanceService.updateAvailableAmount(accountId, transaction.getId());
                            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION,
                                    OperationEnum.CREATE, currentUserId);
                            auditService.saveAudit(audit);
                            return transaction;
                        }
                    }
                    else{
                        throw new Exception("Currencies do not match. Not allowed to transfer");
                    }
                }
                else{
                    throw new Exception("Debit Blocked. Not allowed to receive");
                }
            }
            else{
                throw new Exception("Credit Blocked. Not allowed to send");
            }
        }
        else{
            throw new Exception("Not allowed to make transaction");
        }
    }

    @Transactional
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
    public Transaction approveTransaction(Long id, Long currentUserId) throws Exception {
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.TRANSACTION), currentUserId)) {
            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found"));
            transaction.setStatus(transaction.getNextStatus());
            transaction.setNextStatus(StatusEnum.ACTIVE);
            Transaction activeTransaction = transactionRepository.save(transaction);
            balanceService.updateTotalAmount(transaction.getId());
            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.APPROVE, currentUserId);
            auditService.saveAudit(audit);
            return activeTransaction;
        }
        else{
            throw new Exception("Not allowed to approve");
        }
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
    @Transactional
    @Override
    public Transaction rejectTransaction(Long id, Long currentUserId) throws Exception {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found"));
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.TRANSACTION), currentUserId)) {
            transaction.setStatus(StatusEnum.DELETE);
            transaction.setNextStatus(StatusEnum.DELETE);
            Transaction rejectedTransaction = transactionRepository.save(transaction);
            balanceService.cancelAmountChanges(transaction.getId());
            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.REJECT, currentUserId);
            auditService.saveAudit(audit);
            return rejectedTransaction;
        }
        else{
            throw new Exception("Not allowed to reject");
        }
    }

    /**
     * Authorize transaction
     * Check if the transaction exists by using the given id and throw an exception otherwise
     * Change 'Status' to 'ACTIVE'
     * Update 'Audit' table
     * Update balance of the account that initiated the transaction
     * @param id of the transaction that is authorized
     * @return the rejected transaction
     */
    @Transactional
    @Override
    public Transaction authorizeTransaction(Long id) throws Exception {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found"));
        if(transaction.getStatus() == StatusEnum.AUTHORIZE) {
            transaction.setStatus(StatusEnum.ACTIVE);
            Transaction authorizedTransaction = transactionRepository.save(transaction);
            balanceService.updateTotalAmount(transaction.getId());
            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.AUTHORIZE, 0L);
            auditService.saveAudit(audit);
            return authorizedTransaction;
        }
        else {
            throw new Exception("Authorize not required");
        }
    }

    /**
     * Not-Authorize transaction
     * Check if the transaction exists by using the given id and throw an exception otherwise
     * Change 'Status' and 'NextStatus' to 'DELETE'
     * Update 'Audit' table
     * Update balance of the account that initiated the transaction
     * @param id of the transaction that is authorized
     * @return the rejected transaction
     */
    @Transactional
    @Override
    public Transaction notAuthorizeTransaction(Long id) throws Exception {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found"));
        if(transaction.getStatus() == StatusEnum.AUTHORIZE) {
            transaction.setStatus(StatusEnum.DELETE);
            transaction.setNextStatus(StatusEnum.DELETE);
            Transaction rejectedTransaction = transactionRepository.save(transaction);
            balanceService.cancelAmountChanges(transaction.getId());
            Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.REJECT, 0L);
            auditService.saveAudit(audit);
            return rejectedTransaction;
        }
        else{
            throw new Exception("Authorize not required");
        }
    }
}
