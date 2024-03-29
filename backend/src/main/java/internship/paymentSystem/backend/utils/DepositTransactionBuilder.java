package internship.paymentSystem.backend.utils;

import internship.paymentSystem.backend.DTOs.TransactionBuilderContext;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;

import java.math.BigDecimal;
import java.util.Objects;

public class DepositTransactionBuilder extends BaseTransactionBuilder {

    public DepositTransactionBuilder(TransactionBuilderContext context) {
        super(context);
    }

    @Override
    protected void specificValidations() throws Exception {
        if(Objects.equals(context.getAccountStatus(), AccountStatusEnum.BLOCK_CREDIT)){
            throw new Exception("Credit Blocked. Not allowed to deposit");
        }
    }

    @Override
    protected void fraudValidation() throws Exception{
        //do nothing
        //deposit transactions cannot be frauds
    }

    @Override
    protected Transaction createTransaction(StatusEnum status, StatusEnum nextStatus) {
        BigDecimal amount = context.getTransactionDetails().getAmount();
        Long accountId = context.getTransactionDetails().getAccountID();
        return new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.DEPOSIT, amount,
                accountId, status, nextStatus);
    }

}
