package internship.paymentSystem.backend.DTOs;

import internship.paymentSystem.backend.client.Client;
import internship.paymentSystem.backend.models.Transaction;
import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.repositories.ITransactionRepository;
import internship.paymentSystem.backend.services.interfaces.IAuditService;
import internship.paymentSystem.backend.services.interfaces.IBalanceService;
import internship.paymentSystem.backend.services.interfaces.IEmailService;
import internship.paymentSystem.backend.services.interfaces.ITransactionHistoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionBuilderContext {
    private Transaction transactionDetails;
    private Long currentUserId;
    private Long accountUserId;
    private Long accountId;
    private AccountStatusEnum accountStatus;
    private AccountStatusEnum targetAccountStatus;
    private ITransactionRepository transactionRepository;
    private ITransactionHistoryService transactionHistoryService;
    private IBalanceService balanceService;
    private IAuditService auditService;
    private Client appClient;
    private String currentUserEmail;
    private IEmailService emailService;
}
