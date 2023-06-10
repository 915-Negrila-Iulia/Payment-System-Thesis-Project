package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.bases.PersonEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "personal_information")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Person extends PersonEntity {

}
