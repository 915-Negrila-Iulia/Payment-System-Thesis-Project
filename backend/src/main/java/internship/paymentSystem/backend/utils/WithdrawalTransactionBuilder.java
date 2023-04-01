package internship.paymentSystem.backend.utils;

import internship.paymentSystem.backend.DTOs.TransactionBuilderContext;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;

import java.math.BigDecimal;
import java.util.Objects;

public class WithdrawalTransactionBuilder extends BaseTransactionBuilder {

    public WithdrawalTransactionBuilder(TransactionBuilderContext context) {
        super(context);
    }

    @Override
    protected void specificValidations() throws Exception {
        if(Objects.equals(context.getAccountStatus(), AccountStatusEnum.BLOCK_DEBIT)){
            throw new Exception("Debit Blocked. Not allowed to withdraw");
        }
    }

    @Override
    protected Transaction createTransaction() {
        BigDecimal amount = context.getTransactionDetails().getAmount();
        Long accountId = context.getTransactionDetails().getAccountID();
        return new Transaction(TypeTransactionEnum.INTERNAL, ActionTransactionEnum.WITHDRAWAL, amount,
                accountId, StatusEnum.APPROVE, StatusEnum.ACTIVE);
    }

}
