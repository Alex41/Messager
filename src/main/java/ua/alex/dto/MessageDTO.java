package ua.alex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String text;
    private Long unixTime;
    private Long chatId;
    private UserDTO user;
    private Boolean isRead;
    private Boolean isReads;
}
