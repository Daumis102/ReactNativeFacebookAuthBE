package net.trendd.App.domain.responses;

import net.trendd.App.domain.AppUser;
import net.trendd.App.domain.UserRole;

public record UserResponse(Integer id, String facebookId, UserRole userRole, String firstName,
                           String lastName) {

    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(),
                                user.getFacebookId(),
                                user.getUserRole(),
                                user.getFirstName(),
                                user.getLastName());
    }
}
