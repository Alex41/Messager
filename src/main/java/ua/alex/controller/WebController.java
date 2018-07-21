package ua.alex.controller;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.alex.dto.ChatDTO;
import ua.alex.exceptions.UserNotFoundException;
import ua.alex.model.Chat;
import ua.alex.model.UserModel;
import ua.alex.service.MessageService;
import ua.alex.service.UserService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@AllArgsConstructor
public class WebController {
    private final UserService userService;
    //private final ChatService chatService;
    private final MessageService messageService;


    @GetMapping("/")
    public String index(Model model) {
        UserModel user;
        try {
            user = getUser();
        } catch (UserNotFoundException e) {
            return "login";
        }
        Set<Chat> chats = user.getChatList();

        List<ChatDTO> cloneList = new ArrayList<>();
        for (Chat chat : chats) {
            ChatDTO chatDTO = chat.toDTO(user);
            chatDTO.getUserModelList().removeIf(userDTO -> user.toDTO().equals(userDTO));
            cloneList.add(chatDTO);
        }

        model.addAttribute("unixTime", System.currentTimeMillis());
        model.addAttribute("chatDTO", cloneList);
        model.addAttribute("messageService", messageService);
        model.addAttribute("user", user);
        return "messages";


    }

    @GetMapping("getImage/{login}.jpg")
    public void getImage(HttpServletResponse response, @PathVariable String login) throws IOException {
        @Cleanup ServletOutputStream out = response.getOutputStream();
        try {
            response.setContentType("image/jpeg");
            out.write(userService.getUserByLogin(login).getPhoto());
        } catch (NullPointerException e) {
            @Cleanup FileInputStream fis = new FileInputStream("src/main/resources/static/image.jpg");
            IOUtils.copy(fis, out);
        }
    }

    private UserModel getUser() throws UserNotFoundException {
        try {
            User o = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userService.getUserByLogin(o.getUsername());
        } catch (ClassCastException e) {
            throw new UserNotFoundException();
        }
    }
}
