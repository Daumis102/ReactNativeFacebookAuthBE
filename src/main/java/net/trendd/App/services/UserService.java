package net.trendd.App.services;

import net.trendd.App.domain.AppUser;
import net.trendd.App.repository.UsersRepository;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    public final UsersRepository usersRepository;

    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public AppUser getUserByFacebookId(String facebookId) {
        return usersRepository.getUserByFacebookId(facebookId);
    }

    public AppUser getUserById(String userId) {
        return usersRepository.getUserById(userId);
    }

}
