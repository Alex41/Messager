package ua.alex.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import ua.alex.dto.MessageDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Getter
@NoArgsConstructor
@ToString(of = {"text", "unixTime"})
public class Message {
    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @Column(length = 2000)
    private String text;

    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    private UserModel userModelCreated;

    @Setter
    private Long unixTime;

    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    private Chat chat;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "userUnread",
            joinColumns = @JoinColumn(name = "mIdUnread"),
            inverseJoinColumns = @JoinColumn(name = "userIdUnread"))
    private Set<UserModel> userUnread = new HashSet<>();

    public void addUnreadUser(UserModel user) {
        userUnread.add(user);
    }

    public MessageDTO toDTO(UserModel user) {
        return new MessageDTO(
                getId(),
                getText(),
                getUnixTime(),
                chat.getId(),
                userModelCreated.toDTO(),
                !userUnread.contains(user),
                getUserUnread().size() != (chat.getUserModelList().size() - 1));
    }

    public static Message of(String text, Chat chat, UserModel sender) {
        Message message = new Message();
        message.setChat(chat);
        message.setUnixTime(System.currentTimeMillis());
        message.setUserModelCreated(sender);
        message.setText(text);
        return message;
    }
}
