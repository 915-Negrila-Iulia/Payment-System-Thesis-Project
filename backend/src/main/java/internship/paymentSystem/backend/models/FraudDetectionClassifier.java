package internship.paymentSystem.backend.models;

import internship.paymentSystem.backend.models.enums.MLClassifierType;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "fraud_detection_classifier")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudDetectionClassifier {
    @Id
    @ColumnDefault(value = "0")
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'OVERALL'")
    private MLClassifierType classifierType;
}
