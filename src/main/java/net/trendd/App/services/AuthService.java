package net.trendd.App.services;

import net.trendd.App.controllers.exceptions.FacebookInvalidTokenException;
import net.trendd.App.controllers.exceptions.UserNotFoundException;
import net.trendd.App.domain.AppUser;
import net.trendd.App.domain.responses.FacebookMeResponse;
import net.trendd.App.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    private final FacebookService facebookService;
    private final JwtTokenService jwtTokenService;
    private final UsersRepository usersRepository;
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(FacebookService facebookService,
                       JwtTokenService jwtTokenService,
                       UsersRepository usersRepository) {
        this.facebookService = facebookService;
        this.jwtTokenService = jwtTokenService;
        this.usersRepository = usersRepository;
    }

    public String createToken(String shortTermFbToken) throws FacebookInvalidTokenException {
        String facebookLongToken = facebookService.getAccessToken(shortTermFbToken);
        FacebookMeResponse facebookUser = facebookService.getUserDetails(facebookLongToken);
        AppUser appUser = usersRepository.getUserByFacebookId(facebookUser.id());

        if (appUser != null) {
            appUser = appUser.toBuilder()
                             .fbToken(facebookLongToken)
                             .firstName(facebookUser.firstName())
                             .lastName(facebookUser.lastName())
                             .build();
        } else {
            appUser = AppUser.builder()
                             .facebookId(facebookUser.id())
                             .fbToken(facebookLongToken)
                             .firstName(facebookUser.firstName())
                             .lastName(facebookUser.lastName())
                             .build();
        }
        appUser = usersRepository.createOrUpdate(appUser);
        if (appUser == null) {
            throw new UserNotFoundException(facebookUser.id());
        }
        logger.debug("User found:");
        logger.debug(facebookUser.toString());
        Map<String, List<?>> claims = new HashMap<>();
        claims.put("roles", List.of(appUser.getUserRole()));

        String beToken = jwtTokenService.generateToken(appUser.getId().toString(), claims);
        logger.debug("Token generated successfully");
        return beToken;
    }

}
