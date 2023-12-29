package net.trendd.App.domain;

import lombok.*;
import nu.studer.sample.tables.records.UsersRecord;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public final class AppUser {
    private final Integer id;
    private final String facebookId;
    private final UserRole userRole;
    private String firstName;
    private String lastName;
    @Setter
    private String fbToken;

    public static AppUser from(UsersRecord usersRecord) {
        UserRole userRole = usersRecord.getUserRole() != null ? UserRole.values()[usersRecord.getUserRole()] : null;
        return new AppUser(usersRecord.getId(),
                           usersRecord.getFbId(),
                           userRole,
                           usersRecord.getFirstName(),
                           usersRecord.getLastName(),
                           usersRecord.getFbToken());
    }
}
