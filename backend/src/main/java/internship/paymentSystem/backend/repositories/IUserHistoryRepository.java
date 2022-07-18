package internship.paymentSystem.backend.repositories;

import internship.paymentSystem.backend.models.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserHistoryRepository extends JpaRepository<UserHistory,Long> {
}
