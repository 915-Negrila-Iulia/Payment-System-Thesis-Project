package com.example.backend.models;

import com.example.backend.models.bases.AccountEntity;
import com.example.backend.models.enumerations.AccountStatusEnum;
import com.example.backend.models.enumerations.StatusEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "account_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AccountHistory extends AccountEntity {

    @Column(name = "account_id")
    private Long accountID;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date timestamp;

    public AccountHistory(String iban, String countryCode, String bankCode, String currency, Long personID,
                          StatusEnum status, StatusEnum nextStatus, AccountStatusEnum accountStatus, Long accountID) {
        super(status, nextStatus, iban, countryCode, bankCode, currency, personID, accountStatus);
        this.accountID = accountID;
    }

    public AccountHistory(String iban, String countryCode, String bankCode, String currency, AccountStatusEnum accountStatus) {
        super(iban, countryCode, bankCode, currency, accountStatus);
    }

}
