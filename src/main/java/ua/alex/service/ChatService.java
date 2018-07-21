package ua.alex.service;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.alex.model.Chat;
import ua.alex.model.Message;
import ua.alex.model.UserModel;
import ua.alex.repository.ChatRepository;
import ua.alex.response.ReadResponse;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Boolean createChat(UserModel user, String... users) {
        try {
            Chat chat = new Chat();
            chat.addUser(user);
            for (String s : users) {
                chat.addUser(userService.getUserByLogin(s));
            }
            chatRepository.save(chat);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional(readOnly = true)
    public Chat getChatById(Long chatId) {
        return chatRepository.getChatById(chatId);
    }

    @Transactional
    public void readAllMessagesFromChat(Chat chat, UserModel user) {
        var messages = new ArrayList<Message>(); // else throw ConcurrentModificationException
        for (Message message : user.getUnreadMessage()) {
            if (message.getChat().equals(chat)) {
                messages.add(message);
            }
        }
        for (Message message: messages) {
            user.addReadMessage(message);
            message.getUserUnread().remove(user);
        }

        // socket
        for (UserModel u : chat.getUserModelList()) {
            if (!user.equals(u)){
                messagingTemplate.convertAndSendToUser(u.getLogin(), "/queue/readMessage",
                        new ReadResponse(chat.getId()));
            }
        }

    }

    @Transactional
    public void saveChat(Chat chat) {
        chatRepository.saveAndFlush(chat);
    }

}
