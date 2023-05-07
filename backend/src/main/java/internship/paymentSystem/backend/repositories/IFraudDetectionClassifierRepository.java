package internship.paymentSystem.backend.repositories;

import internship.paymentSystem.backend.models.FraudDetectionClassifier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFraudDetectionClassifierRepository extends JpaRepository<FraudDetectionClassifier,Long> {
}
