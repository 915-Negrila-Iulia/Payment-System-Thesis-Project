package internship.paymentSystem.backend.utils;

import internship.paymentSystem.backend.DTOs.TransactionBuilderContext;
import internship.paymentSystem.backend.customExceptions.FraudException;
import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.Balance;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.*;
import internship.paymentSystem.backend.services.TransactionService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.mail.SimpleMailMessage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * based on Template Design Pattern
 * abstract superclass that defines a set of specific steps (skeleton)
 * regarding the creation of a transaction object
 * subclasses (WithdrawTransaction, DepositTransaction, TransferTransaction) will
 * override some steps without changing the structure
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseTransactionBuilder {

    TransactionBuilderContext context;

    BaseTransactionBuilder(TransactionBuilderContext context){
        this.context = context;
    }

    public final Transaction buildTransaction() throws Exception {
        validations();
        specificValidations();
        fraudValidation();
        Transaction transaction = createTransaction(StatusEnum.APPROVE, StatusEnum.ACTIVE);
        saveTransaction(transaction);
        updateBalance(transaction);
        saveAudit(transaction);
        return transaction;
    }

    private void validations() throws Exception{
        if(!Objects.equals(context.getAccountUserId(),context.getCurrentUserId())){
            throw new Exception("Not allowed to make transaction. Please use your own account");
        }
        if(Objects.equals(context.getAccountStatus(),AccountStatusEnum.BLOCKED)){
            throw new Exception("Account Blocked. Not allowed to perform operation");
        }
        if(context.getTransactionDetails().getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new Exception("Amount must be greater than 0");
        }
    }

    private void sendEmailForSuspectTransaction(Transaction suspectTransaction, LocalDateTime timestamp){
        String toEmail = context.getCurrentUserEmail();
        String subject = "Confirmation required for suspicious transaction";
        String message = "Dear account holder,\n\nWe are writing to inform you that a recent transaction on your " +
                "account has been flagged as potentially fraudulent. The transaction In question is as follows:\n\n" +
                "Transaction date: "+ timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                "Transaction amount: "+ suspectTransaction.getAmount() + " EUR\n\n" +
                "As part of our fraud prevention measures, we are required to confirm that you authorized this transaction " +
                "within the next 2 minutes, otherwise the transaction will automatically expire. " +
                "Please click the link below to confirm the transaction:\n"+
                "http://localhost:8080/api/transactions/confirm-suspect-transaction/" +
                suspectTransaction.getReference() + "\n\n" +
                "If you did not authorize this transaction, please contact us immediately to report the fraudulent " +
                "activity and take steps to secure your account.\n\nThank you for your prompt attention to this matter.\n\n" +
                "Sincerely,\nPayment System Team";
        context.getEmailService().sendEmail(toEmail, subject, message);
    }

    protected void fraudValidation() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        int timeInHours = now.getHour();
        Long accountId = context.getTransactionDetails().getAccountID();
        Long targetAccountId = context.getTransactionDetails().getTargetAccountID();
        BigDecimal amount = context.getTransactionDetails().getAmount();
        ActionTransactionEnum type = context.getTransactionDetails().getAction();
        BigDecimal oldbalanceOrg = context.getBalanceService().getCurrentBalance(accountId).getTotal();
        BigDecimal oldbalanceDest = context.getBalanceService().getCurrentBalance(targetAccountId).getTotal();
        List<Object> responseList = context.getAppClient().isFraudCheck(timeInHours, type, amount, oldbalanceOrg,
                oldbalanceOrg.subtract(amount), oldbalanceDest, oldbalanceDest.add(amount));
        float fraudProbability = (float) responseList.get(0);
        String predictionResult = (String) responseList.get(1);
        System.out.println(fraudProbability + " => " + predictionResult);
        System.out.println("account id and target id: " + accountId + ", " + targetAccountId);
        if(Objects.equals(predictionResult, "Fraud")){
            Transaction suspectTransaction = createTransaction(StatusEnum.SUSPECTED_FRAUD, StatusEnum.FRAUD);
            saveTransaction(suspectTransaction);
            Audit audit = saveAudit(suspectTransaction);
            sendEmailForSuspectTransaction(suspectTransaction, audit.getTimestamp());
            throw new FraudException("Transaction suspected as fraud. Please check your email for further details.",
                    fraudProbability);
        }
    }

    protected abstract void specificValidations() throws Exception;

    protected abstract Transaction createTransaction(StatusEnum status, StatusEnum nextStatus);

    private void saveTransaction(Transaction transaction){
        context.getTransactionRepository().save(transaction);
        context.getTransactionHistoryService().saveTransactionHistory(transaction);
    }

    private void updateBalance(Transaction transaction){
        Long accountId = context.getTransactionDetails().getAccountID();
        context.getBalanceService().updateAvailableAmount(accountId, transaction.getId());
    }

    private Audit saveAudit(Transaction transaction){
        Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE,
                context.getCurrentUserId());
        return context.getAuditService().saveAudit(audit);
    }
}
