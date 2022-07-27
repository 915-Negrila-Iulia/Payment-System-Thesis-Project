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
import java.time.LocalDateTime;

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
    private Double amount;

    @Column(name = "account_id")
    private Long accountID;

    @Column(name = "target_account_id")
    private Long targetAccountID;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime timestamp;

    public TransactionEntity(TypeTransactionEnum type, ActionTransactionEnum action, Double amount, Long accountID,
                       StatusEnum status, StatusEnum nextStatus) {
        super(status, nextStatus);
        this.type = type;
        this.action = action;
        this.amount = amount;
        this.accountID = accountID;
    }

}
