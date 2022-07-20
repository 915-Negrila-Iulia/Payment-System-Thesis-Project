package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.bases.BaseEntity;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserHistory extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime timestamp;

    public UserHistory(String username, String email, String password, StatusEnum status, StatusEnum nextStatus, Long userID) {
        super(status, nextStatus);
        this.username = username;
        this.email = email;
        this.password = password;
        this.userID = userID;
    }

    public UserHistory(String email) {
        this.email = email;
    }
}
