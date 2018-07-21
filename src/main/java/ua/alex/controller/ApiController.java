package ua.alex.controller;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.alex.dto.ChatDTO;
import ua.alex.dto.MessageDTO;
import ua.alex.dto.UserDTO;
import ua.alex.exceptions.UserIsNotAuthorized;
import ua.alex.exceptions.UserIsNotMember;
import ua.alex.exceptions.UserNotFoundException;
import ua.alex.model.Chat;
import ua.alex.model.Message;
import ua.alex.model.UserModel;
import ua.alex.repository.UserRepository;
import ua.alex.response.ChatResponse;
import ua.alex.response.ChatSearchResponse;
import ua.alex.response.MessageResponse;
import ua.alex.response.SuccessResponse;
import ua.alex.service.ChatService;
import ua.alex.service.MessageService;
import ua.alex.service.UserService;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PreRemove;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@AllArgsConstructor
public class ApiController {
    private final MessageService messageService;
    private final ChatService chatService;
    private final UserService userService;
    private static List<Character> characters =
            Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
                    'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
                    'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                    's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '_',
                    '-', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', '0');

    @RequestMapping(value = "isUserExist", method = {POST, GET})
    public SuccessResponse checkUser(@RequestParam(name = "username") String login) {
        login = login.trim();
        if (login.length() == 0)
            return new SuccessResponse(1, 0, "BAD NICK");

        for (char c : login.toCharArray()) {
            if (!characters.contains(c)) {
                return new SuccessResponse(1, 0, "BAD NICK");
            }
        }

        if (userService.isUserExist(login)) {
            return SuccessResponse.userIsAlreadyExist();
        } else return SuccessResponse.ok();
    }

    @RequestMapping(value = "readMessages", method = {POST, GET})
    public MessageResponse readMessages(@RequestParam Long chatId, @RequestParam(required = false, name = "mid") Long messageId) throws UserIsNotMember, UserIsNotAuthorized {
        UserModel user = getUser();
        Chat chat = chatService.getChatById(chatId);
        List<MessageDTO> messageDTOList = new ArrayList<>();
        List<Message> messages;

        if (messageId == null)
            messages = messageService.getMessagesByChat(user, chat);
        else
            messages = messageService.getByChatAndIdGreaterThanId(chat, messageId, user);

        userService.save(user);
        messages.forEach(message -> messageDTOList.add(message.toDTO(user)));
        chatService.readAllMessagesFromChat(chat, user);
        return new MessageResponse(messageDTOList, 0);
    }

    @RequestMapping(value = "writeMessage", method = {GET, POST})
    public SuccessResponse writeMessage(@RequestParam Long chatId,
                                        @RequestParam String text) throws UserIsNotAuthorized {
        text = text.trim();
        if (text.equals("")) return SuccessResponse.emptyMessage();

        UserModel user = getUser();
        Chat chat = chatService.getChatById(chatId);

        try {
            messageService.sendMessage(text, chat, user);
        } catch (UserIsNotMember userIsNotMember) {
            return SuccessResponse.userNonMember();
        }

        return SuccessResponse.ok();
    }

    @RequestMapping(value = "new", method = {RequestMethod.GET, POST})
    public SuccessResponse createNewUser(@RequestParam String password,
                                         @RequestParam String login) {
        login = login.trim();

        if (login.length() == 0)
            return new SuccessResponse(1, 0, "BAD NICK");

        for (char c : login.toCharArray()) {
            if (!characters.contains(c)) {
                return new SuccessResponse(1, 0, "BAD NICK");
            }
        }

        UserModel user = new UserModel();
        user.setLogin(login);
        user.setPassword(password);
        if (userService.addUser(user)) {
            return SuccessResponse.ok();
        } else return SuccessResponse.userIsAlreadyExist();
    }

    @Deprecated
    @GetMapping("result")
    public SuccessResponse result(@RequestParam(required = false, defaultValue = "0") Integer error,
                                  @RequestParam(required = false, defaultValue = "0") Integer success) {
        return new SuccessResponse(error, success);
    }

    @RequestMapping(value = "createChat", method = {GET, POST})
    public ChatResponse createChat(@RequestParam String login) throws UserIsNotAuthorized {  //TODO array users
        UserModel user = getUser();
        for (Chat chat : user.getChatList()) {
            if (chat.getIsPublicChat()) continue;
            for (UserModel model : chat.getUserModelList()) {
                if (model.getLogin().equals(login)) {
                    return new ChatResponse(2, 0, "CHAT ALREADY EXIST", chat.toDTO(user), null);
                }
            }
        }
        if (userService.isUserExist(login)) {
            Chat chat = new Chat();
            chat.addUser(user);
            chat.addUser(userService.getUserByLogin(login));
            chat.setIsPublicChat(false);
            chat.setCreaterUser(user);
            chatService.saveChat(chat);
            return new ChatResponse(0, 1, "OK", chat.toDTO(user), 0L);
        } else return ChatResponse.userNonFound();
    }

    @RequestMapping(value = "getChats", method = {POST, GET})
    public List<ChatDTO> chats() throws UserIsNotAuthorized {
        UserModel user = getUser();
        var chatsDTO = new ArrayList<ChatDTO>();
        user.getChatList().forEach(chat -> chatsDTO.add(chat.toDTO(user)));
        return chatsDTO;
    }

    @RequestMapping(value = "userInfo", method = {POST, GET})
    public UserDTO userInfo(@RequestParam String login) {
        return userService.getUserByLogin(login).toDTO();
    }

    @RequestMapping(value = "search", method = {POST, GET})
    @SneakyThrows(UserIsNotAuthorized.class)
    public ChatSearchResponse search(@RequestParam String login) {
        List<UserDTO> search = userService.search(login);
        search.remove(getUser().toDTO());
        return new ChatSearchResponse(0, 1, search);
    }

    @PostMapping("image")
    public SuccessResponse setImage(@RequestParam MultipartFile file) throws IOException, UserIsNotAuthorized {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        @Cleanup("dispose") Graphics2D graphics = image.createGraphics();
        graphics.drawImage(ImageIO.read(new ByteArrayInputStream(file.getBytes())), 0, 0, 200, 200, null);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", arrayOutputStream);
        UserModel user = getUser();
        user.setPhoto(arrayOutputStream.toByteArray());
        userService.save(user);
        return new SuccessResponse(0, 1);
    }

    private UserModel getUser() throws UserIsNotAuthorized {
        try {
            User o = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userService.getUserByLogin(o.getUsername());
        } catch (ClassCastException e) {
            throw UserIsNotAuthorized.empty();
        }
    }


}
