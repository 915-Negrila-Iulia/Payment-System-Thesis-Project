package com.example.backend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
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

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "OPEN")
    private AccountStatusEnum accountStatus;
}
