package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "iban")
    private String iban;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "currency")
    private String currency;

    @Column(name = "person_id")
    private Long personID;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "APPROVE")
    private StatusEnum status;

    @Column(name = "next_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "ACTIVE")
    private StatusEnum nextStatus;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "OPEN")
    private AccountStatusEnum accountStatus;

    @Column(name = "account_id")
    private Long accountID;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime timestamp;

    public AccountHistory(String iban, String countryCode, String bankCode, String currency, Long personID,
                          StatusEnum status, StatusEnum nextStatus, AccountStatusEnum accountStatus, Long accountID) {
        this.iban = iban;
        this.countryCode = countryCode;
        this.bankCode = bankCode;
        this.currency = currency;
        this.personID = personID;
        this.status = status;
        this.nextStatus = nextStatus;
        this.accountStatus = accountStatus;
        this.accountID = accountID;
    }

    public AccountHistory(String iban, String countryCode, String bankCode, String currency, AccountStatusEnum accountStatus) {
        this.iban = iban;
        this.countryCode = countryCode;
        this.bankCode = bankCode;
        this.currency = currency;
        this.accountStatus = accountStatus;
    }
}
