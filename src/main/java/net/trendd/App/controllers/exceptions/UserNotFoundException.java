package net.trendd.App.controllers.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String facebookId) {
        super("User with facebook Id: %s not found".formatted(facebookId));
    }

    public UserNotFoundException(Integer id) {
        super("User with Id: %s not found".formatted(id));
    }
}
