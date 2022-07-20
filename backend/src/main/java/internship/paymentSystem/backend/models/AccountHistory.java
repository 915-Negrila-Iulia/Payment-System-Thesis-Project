package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.bases.AccountEntity;
import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private LocalDateTime timestamp;

    public AccountHistory(String iban, String countryCode, String bankCode, String currency, Long personID,
                          StatusEnum status, StatusEnum nextStatus, AccountStatusEnum accountStatus, Long accountID) {
        super(status, nextStatus, iban, countryCode, bankCode, currency, personID, accountStatus);
        this.accountID = accountID;
    }

    public AccountHistory(String iban, String countryCode, String bankCode, String currency, AccountStatusEnum accountStatus) {
        super(iban, countryCode, bankCode, currency, accountStatus);
    }

}
