package ua.alex.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.alex.model.Chat;
import ua.alex.model.Message;
import ua.alex.model.UserModel;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message getFirstByChatOrderByIdDesc(Chat chat);

    List<Message> getFirst10ByChatAndIdLessThanOrderByIdDesc(Chat chat, Long mid);

    List<Message> getFirst10ByChatOrderByIdDesc(Chat chat);



}
