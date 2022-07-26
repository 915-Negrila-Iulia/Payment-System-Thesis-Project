package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.bases.BaseEntity;
import internship.paymentSystem.backend.models.bases.TransactionEntity;
import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Transaction extends TransactionEntity {

    public Transaction(TypeTransactionEnum type, ActionTransactionEnum action, Double amount, Long accountID,
                       StatusEnum status, StatusEnum nextStatus) {
        super(type, action, amount, accountID, status, nextStatus);
    }

    public Transaction() {

    }

}

