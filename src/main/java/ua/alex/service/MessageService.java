package ua.alex.service;

import lombok.AllArgsConstructor;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.alex.exceptions.UserIsNotMember;
import ua.alex.model.Chat;
import ua.alex.model.Message;
import ua.alex.model.UserModel;
import ua.alex.repository.ChatRepository;
import ua.alex.repository.MessageRepository;
import ua.alex.response.MessageResponse;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @Transactional(readOnly = true)
    public List<Message> getMessagesByChat(UserModel user, Chat chat) throws UserIsNotMember {
        if (chat.getUserModelList().contains(user)) {
            return messageRepository.getFirst10ByChatOrderByIdDesc(chat);
        } else throw new UserIsNotMember();
    }

    @Transactional
    public void save(Message message) {
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public Message getLastMessageByChatID(Long id) {
        try {
            return messageRepository.getFirstByChatOrderByIdDesc(chatService.getChatById(id));
        } catch (SpelEvaluationException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Message> getByChatAndIdGreaterThanId(Chat chat, Long from, UserModel user) throws UserIsNotMember {
        if (chat.getUserModelList().contains(user)) {
            return messageRepository.getFirst10ByChatAndIdLessThanOrderByIdDesc(chat, from);
        } else throw new UserIsNotMember();
    }

    @Transactional
    public Message sendMessage(String text, Chat chat, UserModel sender) throws UserIsNotMember {
        if (!chat.getUserModelList().contains(sender)) throw UserIsNotMember.empty();
        Message message = Message.of(text, chat, sender);

        for (UserModel user : chat.getUserModelList()) {
            if (!user.equals(sender)) message.addUnreadUser(user);
        }
        messageRepository.saveAndFlush(message);

        // send to socket users
        for (UserModel user : chat.getUserModelList()) {
            messagingTemplate.convertAndSendToUser(user.getLogin(), "/queue/reply",
                    new MessageResponse(Collections.singletonList(message.toDTO(user)), 0));
        }
        chatService.readAllMessagesFromChat(chat, sender);

        return message;
    }
}
