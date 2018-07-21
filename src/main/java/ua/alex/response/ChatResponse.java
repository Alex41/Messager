package ua.alex.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ua.alex.dto.ChatDTO;

@Getter
@AllArgsConstructor
public class ChatResponse implements Response{
    final Integer error;
    final Integer success;
    final String desk;
    final ChatDTO chat;
    final Long unreadCount;

    public static ChatResponse userNonFound(){
        return new ChatResponse(1, 0, "USER NON FOUND", null, null);
    }
}
