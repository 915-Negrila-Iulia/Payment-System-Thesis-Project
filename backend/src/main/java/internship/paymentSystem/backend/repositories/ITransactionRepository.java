package internship.paymentSystem.backend.repositories;

import internship.paymentSystem.backend.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction,Long> {
    Transaction findByReference(String reference);
}
