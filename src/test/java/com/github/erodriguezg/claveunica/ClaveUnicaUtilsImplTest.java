package com.github.erodriguezg.claveunica;

import com.github.erodriguezg.claveunica.dto.ConfiguracionClienteDto;
import com.github.erodriguezg.claveunica.dto.InfoCiudadanoDto;
import com.github.erodriguezg.claveunica.dto.TokenAccesoDto;
import com.github.erodriguezg.claveunica.exceptions.ClaveUnicaException;
import com.github.erodriguezg.claveunica.exceptions.UserInfoClaveUnicaException;
import com.github.erodriguezg.http.HttpClientUtils;
import com.github.erodriguezg.http.dto.HttpResponseDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClaveUnicaUtilsImplTest {

    private static final String INFO_USER_JSON = "{ \"sub\": \"2\", \"RolUnico\": { \"numero\": 55555555, \"DV\": \"5\", \"tipo\": \"RUN\" }, " +
            "\"name\": { \"nombres\": [ \"Maria\", \"Carmen\", \"De\", \"Los\", \"Angeles\"], " +
            "\"apellidos\": [\"Del\", \"Rio\", \"Gonzalez\"] } }";


    private static final String TOKEN_ACCESO_JSON =
            "{ \n" +
                    "\t\"access_token\": \"eb7fc75fb7ee47d8a351f055b69e62fd\", \n" +
                    "\t\"token_type\": \"bearer\", \n" +
                    "\t\"expires_in\": 3600, \n" +
                    "\t\"refresh_token\": \"fe59fc078e364bdca2b06c7802b1c958\", \n" +
                    "\t\"id_token\": \"eyJhbGcckTMBEraqh0qmcMOV1oC4VcFddKg\"\n" +
                    "}\n";

    @Mock
    private ConfiguracionClienteDto configDto;

    @Mock
    private HttpClientUtils httpClientUtils;

    @InjectMocks
    private ClaveUnicaUtilsImpl claveUnicaUtilsImpl;

    @Test
    public void solicitarInfoCiudadanoTest() throws UserInfoClaveUnicaException {
        InfoCiudadanoDto infoEsperado = new InfoCiudadanoDto();
        infoEsperado.setSub("2");
        infoEsperado.setRolUnico(new InfoCiudadanoDto.RolUnico());
        infoEsperado.getRolUnico().setNumero(55555555);
        infoEsperado.getRolUnico().setDv("5");
        infoEsperado.getRolUnico().setTipo("RUN");
        infoEsperado.setName(new InfoCiudadanoDto.Name());
        infoEsperado.getName().setNombresList(Arrays.asList("Maria", "Carmen", "De", "Los", "Angeles"));
        infoEsperado.getName().setApellidosList(Arrays.asList("Del", "Rio", "Gonzalez"));
        HttpResponseDto responseDto = new HttpResponseDto();
        responseDto.setResponseStatus(200);
        responseDto.setResponseByte(INFO_USER_JSON.getBytes());
        when(httpClientUtils.doRequest(any(), any(), any(), any())).thenReturn(responseDto);
        InfoCiudadanoDto infoCiudadanoDto = claveUnicaUtilsImpl.solicitarInfoCiudadano(new TokenAccesoDto());
        assertThat(infoCiudadanoDto).isEqualToComparingFieldByField(infoEsperado);
    }

    @Test
    public void solicitarTokenAccesoTest() throws ClaveUnicaException {
        TokenAccesoDto tokenEsperado = new TokenAccesoDto();
        tokenEsperado.setAccesToken("eb7fc75fb7ee47d8a351f055b69e62fd");
        tokenEsperado.setTokenType("bearer");
        tokenEsperado.setExpiresIn(3600);
        tokenEsperado.setRefreshToken("fe59fc078e364bdca2b06c7802b1c958");
        tokenEsperado.setIdToken("eyJhbGcckTMBEraqh0qmcMOV1oC4VcFddKg");
        HttpResponseDto responseDto = new HttpResponseDto();
        responseDto.setResponseStatus(200);
        responseDto.setResponseByte(TOKEN_ACCESO_JSON.getBytes());
        when(httpClientUtils.doRequest(any(), any(), any(), any())).thenReturn(responseDto);
        when(configDto.getRedirectUrl()).thenReturn("http://www.google.cl/");
        TokenAccesoDto tokenAccesoDto = claveUnicaUtilsImpl.solicitarTokenAcceso("cualquiera", "state");
        assertThat(tokenAccesoDto).isEqualToComparingFieldByField(tokenEsperado);
    }


}
