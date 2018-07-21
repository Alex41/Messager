package ua.alex.controller;


import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ua.alex.exceptions.UserIsNotMember;
import ua.alex.model.Chat;
import ua.alex.model.Message;
import ua.alex.model.UserModel;
import ua.alex.request.ReadMessageRequest;
import ua.alex.request.SendMessageRequest;
import ua.alex.response.MessageResponse;
import ua.alex.response.ReadResponse;
import ua.alex.service.ChatService;
import ua.alex.service.MessageService;
import ua.alex.service.UserService;

import java.security.Principal;
import java.util.Collections;

@Controller
@AllArgsConstructor
@Transactional
public class SocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;
    private final MessageService messageService;

    @MessageMapping("/message")
    public void message(Principal principal, SendMessageRequest request) throws UserIsNotMember {
        UserModel userModel = userService.getUserByLogin(principal.getName());
        Chat chat = chatService.getChatById(request.getChatId());
        messageService.sendMessage(request.getText(), chat, userModel);
    }

    @MessageMapping("/read")
    public void setAsRead(Principal principal, ReadMessageRequest request) {
        UserModel userModel = userService.getUserByLogin(principal.getName());
        Chat chat = chatService.getChatById(request.getChatId());

        if (!chat.getUserModelList().contains(userModel)) return;

        chatService.readAllMessagesFromChat(chat, userModel);
    }


}
