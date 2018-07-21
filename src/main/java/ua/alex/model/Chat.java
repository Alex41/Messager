package ua.alex.model;

import lombok.*;
import org.springframework.data.domain.Pageable;
import ua.alex.dto.ChatDTO;
import ua.alex.dto.UserDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Table
@Entity
@Getter
@ToString(of = "id")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @Column(nullable = false)
    private Boolean isPublicChat;

    @ManyToMany(cascade = ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "users_chat",
            joinColumns = @JoinColumn(name = "chat_ids"),
            inverseJoinColumns = @JoinColumn(name = "user_ids"))
    private Set<UserModel> userModelList = new HashSet<>();

    @OneToMany(cascade = ALL, mappedBy = "chat")
    private List<Message> messageList = new ArrayList<>();

    @ManyToOne(cascade = ALL)
    @Setter
    private UserModel createrUser;


    public void addUser(UserModel user) {
        userModelList.add(user);
    }

    public void addMessage(Message message) {
        messageList.add(message);
    }

    public ChatDTO toDTO(UserModel user) {
        var users = new ArrayList<UserDTO>();
        getUserModelList().forEach(userModel -> users.add(userModel.toDTO()));

        Long unreadCount = 0L;
        for (Message message : user.getUnreadMessage()) {
            if (message.getChat().equals(this)) unreadCount++;
        }
        return new ChatDTO(getId(), users, getIsPublicChat(), canWrite(user), unreadCount);
    }

    public Boolean canWrite(UserModel user) {
        return (!getIsPublicChat() && getUserModelList().contains(user)) ||
                (getIsPublicChat() && getCreaterUser().equals(user));
    }

}
