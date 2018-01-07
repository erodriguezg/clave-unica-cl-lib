package com.github.erodriguezg.claveunica.exceptions;

public class UserInfoClaveUnicaException extends AutorizacionClaveUnicaException {
    public UserInfoClaveUnicaException(int responseStatus, String responseString) {
        super(responseStatus, responseString);
    }
}

