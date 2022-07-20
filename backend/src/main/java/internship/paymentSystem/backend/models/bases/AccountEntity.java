package internship.paymentSystem.backend.models.bases;

import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AccountEntity extends BaseEntity{

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

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "OPEN")
    private AccountStatusEnum accountStatus;

    public AccountEntity(StatusEnum status, StatusEnum nextStatus, String iban, String countryCode, String bankCode,
                         String currency, Long personID, AccountStatusEnum accountStatus) {
        super(status, nextStatus);
        this.iban = iban;
        this.countryCode = countryCode;
        this.bankCode = bankCode;
        this.currency = currency;
        this.personID = personID;
        this.accountStatus = accountStatus;
    }

    public AccountEntity(String iban, String countryCode, String bankCode, String currency, AccountStatusEnum accountStatus) {
        this.iban = iban;
        this.countryCode = countryCode;
        this.bankCode = bankCode;
        this.currency = currency;
        this.accountStatus = accountStatus;
    }

}
