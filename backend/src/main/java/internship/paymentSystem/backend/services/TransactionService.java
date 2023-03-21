package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.DTOs.StatisticDto;
import internship.paymentSystem.backend.client.Client;
import internship.paymentSystem.backend.models.*;
import internship.paymentSystem.backend.models.bases.TransactionEntity;
import internship.paymentSystem.backend.models.enums.*;
import internship.paymentSystem.backend.repositories.ITransactionRepository;
import internship.paymentSystem.backend.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private ITransactionHistoryService transactionHistoryService;

    @Autowired
    private IBalanceService balanceService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IAuditService auditService;

    @Autowired
    private Client appClient;

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

    @Override
    public List<Transaction> getTransactionsByStatus(StatusEnum filterStatus) {
        return transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getStatus() == filterStatus)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(Long id) {
        return transactionRepository.findAll().stream()
                .filter(transaction -> Objects.equals(transaction.getAccountID(), id))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionHistory> getHistoryOfTransactions() {
        return transactionHistoryService.getHistoryOfTransactions();
    }

    @Override
    public List<TransactionHistory> getHistoryByTransactionId(Long transactionId) {
        return transactionHistoryService.getHistoryByTransactionId(transactionId);
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

    private String getIbanByAccountId(Long accountId){
        return accountService.findAccountById(accountId).get().getIban();
    }

    private String getPersonOfAccount(Long accountId){
        Long personId = accountService.findAccountById(accountId).get().getPersonID();
        return personService.findPersonById(personId).get().getFirstName()+" "+
                personService.findPersonById(personId).get().getLastName();
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
                BigDecimal amount = transactionDetails.getAmount();
                Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.DEPOSIT, amount,
                        accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
                transactionRepository.save(transaction);
                transactionHistoryService.saveTransactionHistory(transaction);
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

    private void fraudCheck(Transaction transaction){
        Long step = 0L;
        BigDecimal amount = transaction.getAmount();
        BigDecimal oldbalanceOrg = balanceService.getCurrentBalance(transaction.getAccountID()).getTotal();
        BigDecimal oldbalanceDest = balanceService.getCurrentBalance(transaction.getTargetAccountID()).getTotal();
        appClient.isFraudCheck(step, amount, oldbalanceOrg, oldbalanceOrg.subtract(amount),
                oldbalanceDest, oldbalanceDest.add(amount));
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
        this.fraudCheck(transactionDetails);
        if(Objects.equals(userId, currentUserId)) {
            if(!(Objects.equals(getStatusByAccountId(accountId),AccountStatusEnum.BLOCK_DEBIT) ||
                    Objects.equals(getStatusByAccountId(accountId),AccountStatusEnum.BLOCKED))) {
                BigDecimal amount = transactionDetails.getAmount();
                Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.WITHDRAWAL, amount,
                        accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
                transactionRepository.save(transaction);
                transactionHistoryService.saveTransactionHistory(transaction);
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

    @Transactional
    Transaction internalTransfer(Transaction transactionDetails, Long accountId, Long targetId, Long currentUserId){
        BigDecimal amount = transactionDetails.getAmount();
        String targetAccountIban = accountService.findAccountById(targetId).get().getIban();
        Transaction transaction = new Transaction(TypeTransactionEnum.INTERNAL,
                ActionTransactionEnum.TRANSFER, amount, accountId, targetId, targetAccountIban,
                StatusEnum.APPROVE, StatusEnum.AUTHORIZE);
        transactionRepository.save(transaction);
        transactionHistoryService.saveTransactionHistory(transaction);
        balanceService.updateAvailableAmount(accountId, transaction.getId());
        Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION,
                OperationEnum.CREATE, currentUserId);
        auditService.saveAudit(audit);
        return transaction;
    }

    private String getBicOfBank(String bankName){
        if(Objects.equals(bankName, "SCOTIA BANK"))
            return "NOSCTTPS";
        else if(Objects.equals(bankName, "REPUBLIC BANK LIMITED"))
            return "RBNKTTPX";
        else if (Objects.equals(bankName, "RBC ROYAL BANK"))
            return "RBTTTTPX";
        return null;
    }

    @Transactional
    Transaction externalTransfer(Transaction transactionDetails, Long accountId, Long currentUserId) throws Exception {
        BigDecimal amount = transactionDetails.getAmount();
        String ibanReceiver = transactionDetails.getTargetIban();
        String bankName = transactionDetails.getBankName();
        String nameReceiver = transactionDetails.getNameReceiver();

        Transaction transaction = new Transaction(TypeTransactionEnum.EXTERNAL,
                ActionTransactionEnum.TRANSFER, amount, accountId, null, ibanReceiver,
                bankName, nameReceiver, StatusEnum.APPROVE, StatusEnum.AUTHORIZE);
        transactionRepository.save(transaction);
        transactionHistoryService.saveTransactionHistory(transaction);
        balanceService.updateAvailableAmount(accountId, transaction.getId());
        Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION,
                OperationEnum.CREATE, currentUserId);
        auditService.saveAudit(audit);

        return transaction;
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
                if(transactionDetails.getType() == TypeTransactionEnum.INTERNAL) {
                    if (!(Objects.equals(getStatusByAccountId(targetId), AccountStatusEnum.BLOCK_DEBIT) ||
                            Objects.equals(getStatusByAccountId(targetId), AccountStatusEnum.BLOCKED))) {
                        if (Objects.equals(getCurrencyByAccountId(accountId), getCurrencyByAccountId(targetId))) {
                            // internal transfer
                            return this.internalTransfer(transactionDetails, accountId, targetId, currentUserId);
                        } else {
                            throw new Exception("Currencies do not match. Not allowed to transfer");
                        }
                    } else {
                        throw new Exception("Debit Blocked. Not allowed to receive");
                    }
                }
                else{
                    // ips external transfer
                    return this.externalTransfer(transactionDetails, accountId, currentUserId);
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
        transactionHistoryService.saveTransactionHistory(transaction);
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
            transactionHistoryService.saveTransactionHistory(transaction);
            transaction.setStatus(transaction.getNextStatus());
            if(transaction.getType() == TypeTransactionEnum.INTERNAL) {
                transaction.setNextStatus(StatusEnum.ACTIVE);
            }
            else { // EXTERNAL transaction -> perform ips request
                transaction.setNextStatus(StatusEnum.AUTHORIZE_IPS);
            }
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
            transactionHistoryService.saveTransactionHistory(transaction);
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

    private Transaction internalAuthorize(Transaction transaction){
        transactionHistoryService.saveTransactionHistory(transaction);
        transaction.setStatus(StatusEnum.ACTIVE);
        Transaction authorizedTransaction = transactionRepository.save(transaction);
        balanceService.updateTotalAmount(transaction.getId());
        Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.AUTHORIZE, 0L);
        auditService.saveAudit(audit);
        return authorizedTransaction;
    }

    private void externalAuthorize(Transaction transaction){
        transactionHistoryService.saveTransactionHistory(transaction);
        transaction.setStatus(StatusEnum.AUTHORIZE_IPS);
        transaction.setNextStatus(StatusEnum.ACTIVE);
        Transaction authorizedTransaction = transactionRepository.save(transaction);
        Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.AUTHORIZE, 0L);
        auditService.saveAudit(audit);
    }

    private Transaction ipsAcceptTransaction(Transaction transaction){
        transactionHistoryService.saveTransactionHistory(transaction);
        transaction.setStatus(StatusEnum.ACTIVE);
        Transaction authorizedIpsTransaction = transactionRepository.save(transaction);
        balanceService.updateTotalAmount(transaction.getId());
        Audit auditIps = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.AUTHORIZE, 0L);
        auditService.saveAudit(auditIps);
        return authorizedIpsTransaction;
    }

    private Transaction ipsRejectTransaction(Transaction transaction){
        transactionHistoryService.saveTransactionHistory(transaction);
        transaction.setStatus(StatusEnum.DELETE);
        transaction.setNextStatus(StatusEnum.DELETE);
        Transaction rejectedTransaction = transactionRepository.save(transaction);
        balanceService.cancelAmountChanges(transaction.getId());
        Audit auditIps = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.REJECT, 0L);
        auditService.saveAudit(auditIps);
        return rejectedTransaction;
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
            if(transaction.getType() == TypeTransactionEnum.INTERNAL) {
                return this.internalAuthorize(transaction);
            }
            else{ // EXTERNAL transaction -> perform ips request
                this.externalAuthorize(transaction);

                BigDecimal amount = transaction.getAmount();
                String referenceTransaction = transaction.getReference();
                String nameSender = this.getPersonOfAccount(transaction.getAccountID());
                String ibanSender = this.getIbanByAccountId(transaction.getAccountID());
                String ibanReceiver = transaction.getTargetIban();
                String bankName = transaction.getBankName();
                String nameReceiver = transaction.getNameReceiver();
                String bicReceiver = this.getBicOfBank(bankName);
                String ipsResponse = appClient.sendPaymentRequestIPS(amount, referenceTransaction,
                        nameSender, ibanSender, bicReceiver, nameReceiver,
                        ibanReceiver);

                // waiting for ips response
                try {
                    sleep(5000);
                }
                catch (Exception e){
                    throw new Exception("Thread sleep not working");
                }

                if(Objects.equals(ipsResponse, "ACSP")){
                    return this.ipsAcceptTransaction(transaction);
                }
                else{
                    return this.ipsRejectTransaction(transaction);
                }
            }
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
            transactionHistoryService.saveTransactionHistory(transaction);
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

    private Long getCountStatisticByStatus(Long accountId,StatusEnum status){
        List<Transaction> transactions = this.getTransactionsByAccountId(accountId);
        return transactions.stream()
                .filter(transaction -> transaction.getStatus() == status)
                .count();
    }

    private BigDecimal getAmountStatisticByStatus(Long accountId, StatusEnum status){
        List<Transaction> transactions = this.getTransactionsByAccountId(accountId);
        return transactions.stream()
                .filter(transaction -> transaction.getStatus() == status)
                .map(TransactionEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<StatisticDto> getStatisticsOfAccount(Long accountId) {
        List<StatisticDto> statistics = new ArrayList<>();
        StatisticDto approveStatus = new StatisticDto(StatusEnum.APPROVE,
                this.getCountStatisticByStatus(accountId,StatusEnum.APPROVE),
                this.getAmountStatisticByStatus(accountId,StatusEnum.APPROVE));
        statistics.add(approveStatus);
        StatisticDto authorizeStatus = new StatisticDto(StatusEnum.AUTHORIZE,
                this.getCountStatisticByStatus(accountId,StatusEnum.AUTHORIZE),
                this.getAmountStatisticByStatus(accountId,StatusEnum.AUTHORIZE));
        statistics.add(authorizeStatus);
        StatisticDto activeStatus = new StatisticDto(StatusEnum.ACTIVE,
                this.getCountStatisticByStatus(accountId,StatusEnum.ACTIVE),
                this.getAmountStatisticByStatus(accountId,StatusEnum.ACTIVE));
        statistics.add(activeStatus);
        StatisticDto deleteStatus = new StatisticDto(StatusEnum.DELETE,
                this.getCountStatisticByStatus(accountId,StatusEnum.DELETE),
                this.getAmountStatisticByStatus(accountId,StatusEnum.DELETE));
        statistics.add(deleteStatus);
        return statistics;
    }
}
