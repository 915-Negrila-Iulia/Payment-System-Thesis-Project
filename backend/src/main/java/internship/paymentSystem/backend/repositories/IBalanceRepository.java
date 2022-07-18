package internship.paymentSystem.backend.repositories;

import internship.paymentSystem.backend.models.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBalanceRepository extends JpaRepository<Balance, Long> {
}
