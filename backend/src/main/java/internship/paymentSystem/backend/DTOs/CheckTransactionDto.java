package internship.paymentSystem.backend.DTOs;

import internship.paymentSystem.backend.models.enums.MLClassifierType;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckTransactionDto {

    private String classifierType;

    private int timeInHours;

    private int type;

    private BigDecimal amount;

    private BigDecimal oldBalanceOrg;

    private BigDecimal newBalanceOrig;

    private BigDecimal oldBalanceDest;

    private BigDecimal newBalanceDest;
}
