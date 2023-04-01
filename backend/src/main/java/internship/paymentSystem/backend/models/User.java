package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.bases.BaseEntity;
import internship.paymentSystem.backend.models.enums.AccountStatusEnum;
import internship.paymentSystem.backend.models.enums.RoleEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "user")
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

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'USER_ROLE'")
    private RoleEnum role;

    public User(String username, String email, String password, RoleEnum role, StatusEnum status, StatusEnum nextStatus) {
        super(status,nextStatus);
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String email){
        this.email = email;
    }

}
