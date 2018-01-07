package com.github.erodriguezg.http.dto;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class HttpResponseDto implements Serializable {

    private byte[] responseByte;

    private Integer responseStatus;

    public byte[] getResponseByte() {
        return responseByte;
    }

    public void setResponseByte(byte[] responseByte) {
        this.responseByte = responseByte;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseString() {
        if(responseByte == null) {
            return null;
        }
        try {
            return new String(this.responseByte, "latin1");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
