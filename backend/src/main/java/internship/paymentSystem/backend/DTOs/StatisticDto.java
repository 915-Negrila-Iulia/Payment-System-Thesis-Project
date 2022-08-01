package internship.paymentSystem.backend.DTOs;

import internship.paymentSystem.backend.models.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDto {

    private StatusEnum status;

    private Long count;

    private BigDecimal amount;

}
