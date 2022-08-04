package internship.paymentSystem.backend.models.bases;

import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class TransactionEntity extends BaseEntity{

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeTransactionEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private ActionTransactionEnum action;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "account_id")
    private Long accountID;

    @Column(name = "target_account_id")
    private Long targetAccountID;

    @Column(name = "target_external_iban")
    private String targetIban;

    @Column(name = "reference")
    private String reference = UUID.randomUUID().toString().replaceAll("-","");

    public TransactionEntity(TypeTransactionEnum type, ActionTransactionEnum action, BigDecimal amount, Long accountID,
                             StatusEnum status, StatusEnum nextStatus) {
        super(status, nextStatus);
        this.type = type;
        this.action = action;
        this.amount = amount;
        this.accountID = accountID;
    }

    public TransactionEntity(TypeTransactionEnum type, ActionTransactionEnum action, BigDecimal amount, Long accountID,
                             Long targetAccountID, String targetIban, StatusEnum status, StatusEnum nextStatus) {
        super(status, nextStatus);
        this.type = type;
        this.action = action;
        this.amount = amount;
        this.accountID = accountID;
        this.targetAccountID = targetAccountID;
        this.targetIban = targetIban;
    }
}
