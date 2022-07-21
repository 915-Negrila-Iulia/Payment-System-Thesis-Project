package internship.paymentSystem.backend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseObjectDto<T> {

    private CurrentUserDto currentUserDto;

    private T object;

}
