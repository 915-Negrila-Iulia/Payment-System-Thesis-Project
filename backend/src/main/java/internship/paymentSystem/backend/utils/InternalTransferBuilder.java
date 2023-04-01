package internship.paymentSystem.backend.utils;

import internship.paymentSystem.backend.DTOs.TransactionBuilderContext;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;

import java.math.BigDecimal;
import java.util.Objects;

public class InternalTransferBuilder extends BaseTransactionBuilder {

    public InternalTransferBuilder(TransactionBuilderContext context) {
        super(context);
    }

    @Override
    protected void specificValidations() throws Exception {
        if(Objects.equals(context.getTargetAccountStatus(),AccountStatusEnum.BLOCKED)){
            throw new Exception("Target Account Blocked. Not allowed to perform operation");
        }
        if(Objects.equals(context.getAccountStatus(), AccountStatusEnum.BLOCK_CREDIT)){
            throw new Exception("Credit Blocked. Not allowed to send");
        }
        if(Objects.equals(context.getTargetAccountStatus(), AccountStatusEnum.BLOCK_DEBIT)){
            throw new Exception("Debit Blocked. Not allowed to receive");
        }
    }

    @Override
    protected Transaction createTransaction() {
        BigDecimal amount = context.getTransactionDetails().getAmount();
        Long targetId = context.getTransactionDetails().getTargetAccountID();
        String targetAccountIban = context.getTransactionDetails().getTargetIban();
        Long accountId = context.getTransactionDetails().getAccountID();
        return new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.TRANSFER, amount,
                accountId, targetId, targetAccountIban, StatusEnum.APPROVE, StatusEnum.AUTHORIZE);
    }

}
