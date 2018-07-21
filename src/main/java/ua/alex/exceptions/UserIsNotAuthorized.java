package ua.alex.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserIsNotAuthorized extends Exception{
    public static UserIsNotAuthorized empty(){
        return new UserIsNotAuthorized();
    }
}
