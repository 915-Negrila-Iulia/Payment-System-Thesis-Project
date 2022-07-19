package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.bases.BaseEntity;
import internship.paymentSystem.backend.models.enumerations.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enumerations.StatusEnum;
import internship.paymentSystem.backend.models.enumerations.TypeTransactionEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Transaction extends BaseEntity {

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

    public Transaction(TypeTransactionEnum type, ActionTransactionEnum action, Double amount, Long accountID,
                       StatusEnum status, StatusEnum nextStatus) {
        super(status, nextStatus);
        this.type = type;
        this.action = action;
        this.amount = amount;
        this.accountID = accountID;
    }
}

