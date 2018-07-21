package ua.alex.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserIsNotMember extends Exception {
    public static UserIsNotMember empty(){
        return new UserIsNotMember();
    }
}
