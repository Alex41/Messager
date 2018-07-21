package ua.alex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.alex.model.Chat;
import ua.alex.model.UserModel;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    @Query("SELECT u FROM UserModel u where u.login = :login")
    UserModel getByLogin(@Param("login") String login);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserModel u WHERE u.login = :login")
    boolean existsByLogin(@Param("login") String login);

    @Query("SELECT u from UserModel u WHERE lower(u.login) LIKE lower(?1) ")
    List<UserModel> getAllByLoginIsLikeOrderByLogin(String login);

}
