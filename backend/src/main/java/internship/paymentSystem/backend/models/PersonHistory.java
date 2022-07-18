package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.bases.PersonEntity;
import internship.paymentSystem.backend.models.enumerations.StatusEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "person_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PersonHistory extends PersonEntity {

    @Column(name = "person_id")
    private Long personID;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime timestamp;

    public PersonHistory(String firstName, String lastName, String address, LocalDate dateOfBirth, String phoneNumber,
                         Long userID, StatusEnum status, StatusEnum nextStatus, Long personID) {
        super(status, nextStatus, firstName, lastName, address, dateOfBirth, phoneNumber, userID);
        this.personID = personID;
    }

    public PersonHistory(String firstName, String lastName, String address, LocalDate dateOfBirth, String phoneNumber) {
        super(firstName, lastName, address, dateOfBirth, phoneNumber);
    }

}
