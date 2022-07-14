package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import java.sql.Types;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "APPROVE")
    private StatusEnum status;

    @Column(name = "next_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "ACTIVE")
    private StatusEnum nextStatus;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime timestamp;

    public Transaction(TypeTransactionEnum type, ActionTransactionEnum action, Double amount, Long accountID,
                       StatusEnum status, StatusEnum nextStatus) {
        this.type = type;
        this.action = action;
        this.amount = amount;
        this.accountID = accountID;
        this.status = status;
        this.nextStatus = nextStatus;
    }
}
