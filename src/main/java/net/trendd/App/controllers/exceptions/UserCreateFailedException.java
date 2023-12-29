package net.trendd.App.controllers.exceptions;

public class UserCreateFailedException extends RuntimeException {
    public UserCreateFailedException(String facebookId){
        super("Failed to create user with facebookId: %s".formatted(facebookId));
    }
}
