package ua.alex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.alex.model.UserModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserDTO {
    private Long id;
    private String login;
}
