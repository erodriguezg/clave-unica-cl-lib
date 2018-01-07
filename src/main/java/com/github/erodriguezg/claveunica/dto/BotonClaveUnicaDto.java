package com.github.erodriguezg.claveunica.dto;

import java.io.Serializable;

public class BotonClaveUnicaDto implements Serializable {
    private String url;
    private String state;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

