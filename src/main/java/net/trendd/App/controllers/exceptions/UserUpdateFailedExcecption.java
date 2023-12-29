package net.trendd.App.controllers.exceptions;

public class UserUpdateFailedExcecption extends RuntimeException {
    public UserUpdateFailedExcecption(Integer userId) {
        super("Updating user with id %s failed".formatted(userId));
    }
}
