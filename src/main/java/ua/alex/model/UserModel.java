package ua.alex.model;

import lombok.*;
import ua.alex.dto.UserDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Entity
@Getter
@NoArgsConstructor
@ToString(of = {"id", "login"})
@EqualsAndHashCode(of = "id")
public class UserModel {
    @Id
    @GeneratedValue
    @Setter
    private Long id;

    @Setter
    private String password;

    @ManyToMany(cascade = ALL, mappedBy = "userModelList")
    private Set<Chat> chatList = new HashSet<>();

    @Setter
    @Enumerated(EnumType.STRING)
    private Roles role;

    @Setter
    private byte[] photo;

    @ManyToMany(mappedBy = "userUnread",cascade = ALL)
    private Set<Message> unreadMessage = new HashSet<>();

    @Setter
    @Column(unique = true, nullable = false)
    private String login;

    public void addReadMessage(Message message) {
        unreadMessage.remove(message);
    }

    public UserDTO toDTO() {
        return new UserDTO(getId(), getLogin());
    }

    @Getter
    @OneToMany(cascade = ALL, mappedBy = "createrUser")
    private List<Chat> adminOf = new ArrayList<>();

}
