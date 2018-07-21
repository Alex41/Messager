package ua.alex.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.alex.dto.UserDTO;
import ua.alex.model.Roles;
import ua.alex.model.UserModel;
import ua.alex.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;


    @Transactional(readOnly = true)
    public UserModel getUserByLogin(String login) {
        return userRepository.getByLogin(login);
    }

    @Transactional
    public boolean addUser(UserModel user) {
        if (userRepository.existsByLogin(user.getLogin()))
            return false;

        String encodePassword = encoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        user.setRole(Roles.USER);
        userRepository.save(user);
        return true;
    }

    public Boolean isUserExist(String username){
        return userRepository.existsByLogin(username);
    }

    @Transactional()
    public void save(UserModel user){
        userRepository.save(user);
    }

    public List<UserDTO> search(String login){
        List<UserDTO> userDTOList = new ArrayList<>();
        userRepository.getAllByLoginIsLikeOrderByLogin(login + '%').forEach(user -> userDTOList.add(user.toDTO()));
        return userDTOList;
    }

}

