package ua.alex.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatDTO {
    private Long id;
    private List<UserDTO> userModelList;
    private Boolean isPublicChat;
    private Boolean canWrite;
    private Long unreadCount;
}
