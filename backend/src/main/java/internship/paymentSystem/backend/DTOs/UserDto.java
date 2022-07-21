package internship.paymentSystem.backend.DTOs;

import internship.paymentSystem.backend.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private CurrentUserDto currentUserDto;

    private User object;

}
