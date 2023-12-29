package net.trendd.App.controllers;

import net.trendd.App.controllers.exceptions.FacebookInvalidTokenException;
import net.trendd.App.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class AuthController {
    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping(path = "/token")
    public ResponseEntity<Void> createToken(@RequestHeader("Authorization") String authorization) throws FacebookInvalidTokenException {
        String newToken = authService.createToken(authorization);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, newToken).build();
    }

    @GetMapping(path = "/refreshToken")
    ResponseEntity<Void> createOrRefreshToken() {
        return ResponseEntity.ok().build();
    }
}
