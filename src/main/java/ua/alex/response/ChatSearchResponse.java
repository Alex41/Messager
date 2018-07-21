package ua.alex.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.alex.dto.UserDTO;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatSearchResponse implements Response {
    final Integer error;
    final Integer success;
    final List<UserDTO> users;

}
