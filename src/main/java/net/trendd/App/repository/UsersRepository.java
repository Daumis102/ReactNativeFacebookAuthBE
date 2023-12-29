package net.trendd.App.repository;

import net.trendd.App.domain.AppUser;
import nu.studer.sample.tables.Users;
import nu.studer.sample.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

@Repository
public class UsersRepository {
    private final DSLContext dsl;
    private final Users usersTable = Users.USERS;

    UsersRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public AppUser getUserByFacebookId(String facebookId) {
        Result<UsersRecord> result = dsl.selectFrom(usersTable).where(usersTable.FB_ID.eq(facebookId)).fetch();
        if (result.size() != 1) {
            return null;
        }
        return AppUser.from(result.getFirst());
    }

    public AppUser getUserById(String userId) {
        Result<UsersRecord> result = dsl.selectFrom(usersTable)
                                        .where(usersTable.ID.eq(Integer.valueOf(userId)))
                                        .fetch();
        if (result.size() != 1) {
            return null;
        }
        return AppUser.from(result.getFirst());
    }

    public AppUser createOrUpdate(AppUser appUser) {
        if (appUser.getId() == null) {
            return createUser(appUser);
        }

        Result<UsersRecord> result = dsl.selectFrom(usersTable).where(usersTable.ID.eq(appUser.getId())).fetch();
        if (result.size() != 1) {
            return null;
        }

        UsersRecord usersRecord = result.getFirst();
        if (appUser.getFacebookId() != null && !usersRecord.getFbId().equals(appUser.getFacebookId())) {
            return null; // User id does not match facebook ID
        }

        return updateUser(usersRecord, appUser);
    }

    private AppUser createUser(AppUser appUser) {
        Result<UsersRecord> result = dsl.insertInto(usersTable,
                                                    usersTable.FB_ID,
                                                    usersTable.FB_TOKEN,
                                                    usersTable.FIRST_NAME,
                                                    usersTable.LAST_NAME)
                                        .values(appUser.getFacebookId(),
                                                appUser.getFbToken(),
                                                appUser.getFirstName(),
                                                appUser.getLastName())
                                        .returning(usersTable.fields())
                                        .fetch();
        if (result.size() != 1) {
            return null;
        }
        return AppUser.from(result.getFirst());
    }

    private AppUser updateUser(UsersRecord usersRecord, AppUser appUser) {
        usersRecord.set(usersTable.FIRST_NAME, appUser.getFirstName());
        usersRecord.set(usersTable.LAST_NAME, appUser.getLastName());
        int numStoredRecords = usersRecord.store();

        if (numStoredRecords == 0) {
            return null;
        }
        return AppUser.from(usersRecord);
    }
}
