package net.trendd.App.controllers.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

public class FacebookOAuthException extends RuntimeException {
    public FacebookOAuthException(HttpStatusCode statusCode, HttpHeaders httpHeaders) {
        super("Facebook oAuth exchange token request returned with %s, headers: %s".formatted(statusCode, httpHeaders.toString()));
    }
}
