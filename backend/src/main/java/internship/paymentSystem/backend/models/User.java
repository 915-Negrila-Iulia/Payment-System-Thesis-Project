package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.bases.BaseEntity;
import internship.paymentSystem.backend.models.enumerations.StatusEnum;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user")
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class User extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    public User(String username, String email, String password, StatusEnum status, StatusEnum nextStatus) {
        super(status,nextStatus);
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String email){
        this.email = email;
    }

}
