package internship.paymentSystem.backend.repositories;

import internship.paymentSystem.backend.models.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuditRepository extends JpaRepository<Audit,Long> {
}
