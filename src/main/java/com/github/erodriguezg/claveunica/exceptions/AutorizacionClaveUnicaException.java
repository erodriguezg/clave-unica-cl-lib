package com.github.erodriguezg.claveunica.exceptions;

public class AutorizacionClaveUnicaException extends ClaveUnicaException {

    private final int responseStatus;

    private final String responseString;

    public AutorizacionClaveUnicaException(int responseStatus, String responseString) {
        super("STATUS: '@status' DETALLE: '@detalle'"
                .replace("@status", Integer.toString(responseStatus) )
                .replace("@detalle", responseString));
        this.responseStatus = responseStatus;
        this.responseString = responseString;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getResponseString() {
        return responseString;
    }

}
