package com.github.erodriguezg.claveunica;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.erodriguezg.claveunica.dto.ConfiguracionClienteDto;
import com.github.erodriguezg.claveunica.dto.InfoCiudadanoDto;
import com.github.erodriguezg.claveunica.dto.TokenAccesoDto;
import com.github.erodriguezg.claveunica.dto.UrlClaveUnicaDto;
import com.github.erodriguezg.claveunica.exceptions.AutorizacionClaveUnicaException;
import com.github.erodriguezg.claveunica.exceptions.ClaveUnicaException;
import com.github.erodriguezg.claveunica.exceptions.UserInfoClaveUnicaException;
import com.github.erodriguezg.http.HttpClientUtils;
import com.github.erodriguezg.http.dto.HttpResponseDto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ClaveUnicaUtilsImpl implements ClaveUnicaUtils {

    private final ConfiguracionClienteDto configDto;

    private final HttpClientUtils httpClientUtils;

    private Supplier<String> stateGenerator;

    private BiFunction<ConfiguracionClienteDto, String, String> urlGenerator;

    public ClaveUnicaUtilsImpl(ConfiguracionClienteDto configDto, HttpClientUtils httpClientUtils) {
        this.configDto = configDto;
        this.httpClientUtils = httpClientUtils;
        this.stateGenerator = this::defaultStateGenerator;
        this.urlGenerator = this::defaultUrlGenerator;
    }

    @Override
    public UrlClaveUnicaDto generarUrlClaveUnica() {
        String state = stateGenerator.get();
        String urlSalida = urlGenerator.apply(this.configDto, state);
        UrlClaveUnicaDto dto = new UrlClaveUnicaDto();
        dto.setState(state);
        dto.setUrl(urlSalida);
        return dto;
    }

    @Override
    public InfoCiudadanoDto solicitarInfoCiudadano(String code, String state) throws ClaveUnicaException {
        return solicitarInfoCiudadano(solicitarTokenAcceso(code, state));
    }

    @Override
    public TokenAccesoDto solicitarTokenAcceso(String code, String state) throws ClaveUnicaException {
        Map<String, String> httpParams = new LinkedHashMap<>();
        httpParams.put("client_id", this.configDto.getClientId());
        httpParams.put("client_secret", this.configDto.getClientSecret());
        httpParams.put("redirect_uri", urlEncoder(this.configDto.getRedirectUrl()));
        httpParams.put("grant_type", "authorization_code");
        httpParams.put("code", code);
        httpParams.put("state", state);
        HttpResponseDto response = this.httpClientUtils.doRequest(
                HttpClientUtils.RequestMethod.POST,
                this.configDto.getEndpoint() + "/openid/token/",
                httpParams, null);
        if (!response.getResponseStatus().equals(200)) {
            throw new AutorizacionClaveUnicaException(response.getResponseStatus(), response.getResponseString());
        }
        return mapRespuestaToken(response.getResponseString());
    }

    @Override
    public InfoCiudadanoDto solicitarInfoCiudadano(TokenAccesoDto tokenAccesoDto) throws UserInfoClaveUnicaException {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Authorization", "Bearer " + tokenAccesoDto.getAccesToken());
        HttpResponseDto response = this.httpClientUtils.doRequest(
                HttpClientUtils.RequestMethod.POST,
                this.configDto.getEndpoint() + "/openid/userinfo/",
                null, headers);
        if (!response.getResponseStatus().equals(200)) {
            throw new UserInfoClaveUnicaException(response.getResponseStatus(), "Usuario no disponible");
        }
        return mapRespuestaInfoCiudadano(response.getResponseString());
    }

    @Override
    public void setStateGenerator(Supplier<String> stateGenerator) {
        this.stateGenerator = stateGenerator;
    }

    @Override
    public void setUrlGenerator(BiFunction<ConfiguracionClienteDto, String, String> urlGenerator) {
        this.urlGenerator = urlGenerator;
    }

    /* privados */

    private String defaultStateGenerator() {
        return UUID.randomUUID().toString();
    }

    private String defaultUrlGenerator(ConfiguracionClienteDto configClienteDto, String state) {
        StringBuilder urlSalida = new StringBuilder();
        urlSalida.append(configClienteDto.getEndpoint() + "/openid/authorize/")
                .append("?client_id=").append(configClienteDto.getClientId())
                .append("&redirect_uri=").append(urlEncoder(configClienteDto.getRedirectUrl()))
                .append("&response_type=").append("code")
                .append("&scope=").append("openid run name")
                .append("&state=").append(state);
        return urlSalida.toString();
    }

    private String urlEncoder(String redirectUrl) {
        try {
            return URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private TokenAccesoDto mapRespuestaToken(String jsonResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonResponse, TokenAccesoDto.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private InfoCiudadanoDto mapRespuestaInfoCiudadano(String jsonResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonResponse, InfoCiudadanoDto.class);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
