package internship.paymentSystem.backend.utils;

import internship.paymentSystem.backend.DTOs.TransactionBuilderContext;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;

import java.math.BigDecimal;
import java.util.Objects;

public class ExternalTransferBuilder extends BaseTransactionBuilder {

    public ExternalTransferBuilder(TransactionBuilderContext context) {
        super(context);
    }

    @Override
    protected void specificValidations() throws Exception {
        if(Objects.equals(context.getAccountStatus(), AccountStatusEnum.BLOCK_CREDIT)){
            throw new Exception("Credit Blocked. Not allowed to send");
        }
    }

    @Override
    protected Transaction createTransaction() {
        BigDecimal amount = context.getTransactionDetails().getAmount();
        String ibanReceiver = context.getTransactionDetails().getTargetIban();
        String bankName = context.getTransactionDetails().getBankName();
        String nameReceiver = context.getTransactionDetails().getNameReceiver();
        Long accountId = context.getTransactionDetails().getAccountID();

        return new Transaction(TypeTransactionEnum.EXTERNAL,
                ActionTransactionEnum.TRANSFER, amount, accountId, null, ibanReceiver,
                bankName, nameReceiver, StatusEnum.APPROVE, StatusEnum.AUTHORIZE);
    }

}
