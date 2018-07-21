package ua.alex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.alex.model.Chat;
import ua.alex.model.UserModel;

import java.util.List;
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.id = :id")
    Chat getChatById(@Param("id") Long id);

}
