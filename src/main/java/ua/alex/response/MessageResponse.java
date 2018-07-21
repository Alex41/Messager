package ua.alex.response;



import lombok.*;
import ua.alex.dto.MessageDTO;
import ua.alex.model.Message;

import java.util.Date;
import java.util.List;
@Getter
@ToString
@AllArgsConstructor
public class MessageResponse implements Response{
    final List<MessageDTO> messages;
    final Integer error;
    final Long time;
    final Integer success;

    public MessageResponse(List<MessageDTO> messages, Integer error) {
        this.messages = messages;
        this.error = error;
        this.time = System.currentTimeMillis();
        this.success = 1;
    }
}
