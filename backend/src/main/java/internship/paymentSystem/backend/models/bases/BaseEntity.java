package internship.paymentSystem.backend.models.bases;

import internship.paymentSystem.backend.models.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "APPROVE")
    private StatusEnum status;

    @Column(name = "next_status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "ACTIVE")
    private StatusEnum nextStatus;

    public BaseEntity(StatusEnum status, StatusEnum nextStatus) {
        this.status = status;
        this.nextStatus = nextStatus;
    }
}
