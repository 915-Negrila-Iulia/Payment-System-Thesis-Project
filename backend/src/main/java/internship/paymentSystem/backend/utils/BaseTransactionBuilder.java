package internship.paymentSystem.backend.utils;

import internship.paymentSystem.backend.DTOs.TransactionBuilderContext;
import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import internship.paymentSystem.backend.models.enums.OperationEnum;
import internship.paymentSystem.backend.services.TransactionService;

import java.util.Objects;

/**
 * based on Template Design Pattern
 * abstract superclass that defines a set of specific steps (skeleton)
 * regarding the creation of a transaction object
 * subclasses (WithdrawTransaction, DepositTransaction, TransferTransaction) will
 * override some steps without changing the structure
 */
public abstract class BaseTransactionBuilder {

    TransactionBuilderContext context;

    BaseTransactionBuilder(TransactionBuilderContext context){
        this.context = context;
    }

    public final Transaction buildTransaction() throws Exception {
        validations();
        specificValidations();
        Transaction transaction = createTransaction();
        saveTransaction(transaction);
        updateBalance(transaction);
        saveAudit(transaction);
        return transaction;
    }

    private void validations() throws Exception{
        if(!Objects.equals(context.getAccountUserId(),context.getCurrentUserId())){
            throw new Exception("Not allowed to make transaction");
        }
        if(Objects.equals(context.getAccountStatus(),AccountStatusEnum.BLOCKED)){
            throw new Exception("Account Blocked. Not allowed to perform operation");
        }
    }

    protected abstract void specificValidations() throws Exception;

    protected abstract Transaction createTransaction();

    private void saveTransaction(Transaction transaction){
        context.getTransactionRepository().save(transaction);
        context.getTransactionHistoryService().saveTransactionHistory(transaction);
    }

    private void updateBalance(Transaction transaction){
        Long accountId = context.getTransactionDetails().getAccountID();
        context.getBalanceService().updateAvailableAmount(accountId, transaction.getId());
    }

    private void saveAudit(Transaction transaction){
        Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE,
                context.getCurrentUserId());
        context.getAuditService().saveAudit(audit);
    }
}
