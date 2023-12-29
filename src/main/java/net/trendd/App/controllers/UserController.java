package net.trendd.App.controllers;

import net.trendd.App.controllers.exceptions.UserNotFoundException;
import net.trendd.App.domain.AppUser;
import net.trendd.App.domain.responses.UserResponse;
import net.trendd.App.services.JwtTokenService;
import net.trendd.App.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    UserController(UserService userService, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @GetMapping("/getAuthenticatedUser")
    ResponseEntity<UserResponse> getUser(@RequestHeader("Authorization") String authorization) {
        String userId = jwtTokenService.getUserIdFromToken(authorization);
        try {
            AppUser appUser = userService.getUserById(userId);
            UserResponse response = UserResponse.from(appUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
