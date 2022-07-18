package internship.paymentSystem.backend.repositories;

import internship.paymentSystem.backend.models.PersonHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonHistoryRepository extends JpaRepository<PersonHistory,Long> {
}
