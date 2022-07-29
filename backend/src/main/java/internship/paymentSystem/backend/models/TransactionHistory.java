package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.bases.TransactionEntity;
import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TransactionHistory extends TransactionEntity {

    @Column(name = "transaction_id")
    private Long transactionID;

    @Column(name = "history_timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime historyTimestamp;

    public TransactionHistory(TypeTransactionEnum type, ActionTransactionEnum action, Double amount, Long accountID,
                              StatusEnum status, StatusEnum nextStatus, Long transactionID) {
        super(type, action, amount, accountID, status, nextStatus);
        this.transactionID = transactionID;
    }

    public TransactionHistory(TypeTransactionEnum type, ActionTransactionEnum action, Double amount, Long accountID,
                              StatusEnum status, StatusEnum nextStatus) {
        super(type, action, amount, accountID, status, nextStatus);
    }

    public TransactionHistory(TypeTransactionEnum type, ActionTransactionEnum action, Double amount, Long accountID,
                       Long targetAccountID, String targetIban, StatusEnum status, StatusEnum nextStatus) {
        super(type, action, amount, accountID, targetAccountID, targetIban, status, nextStatus);
    }

}
