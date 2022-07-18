package internship.paymentSystem.backend.repositories;

import internship.paymentSystem.backend.models.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAccountHistoryRepository extends JpaRepository<AccountHistory,Long> {
}
