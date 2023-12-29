package net.trendd.App.controllers.exceptions;

public class FacebookInvalidTokenException extends Exception {
        public FacebookInvalidTokenException() {
            super("Token provided to Facebook is Invalid");
        }
}
