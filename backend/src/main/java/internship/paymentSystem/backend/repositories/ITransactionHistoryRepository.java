package internship.paymentSystem.backend.repositories;

import internship.paymentSystem.backend.models.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionHistoryRepository extends JpaRepository<TransactionHistory,Long> {
}
