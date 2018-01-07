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
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClaveUnicaUtilsTest {

    private static final String INFO_USER_JSON = "{ \"sub\": \"2\", \"RolUnico\": { \"numero\": 55555555, \"DV\": \"5\", \"tipo\": \"RUN\" }, " +
            "\"name\": { \"nombres\": [ \"Maria\", \"Carmen\", \"De\", \"Los\", \"Angeles\"], " +
            "\"apellidos\": [\"Del\", \"Rio\", \"Gonzalez\"] } }";


    private static final String TOKEN_ACCESO_JSON =
            "{\n" +
                    "    \"access_token\": \"95104ab471534af08683aefa7d0935a3\",\n" +
                    "    \"token_type\": \"bearer\",\n" +
                    "    \"expires_in\": 3600,\n" +
                    "    \"id_token\": \"eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg1ZGVjMDU1MjZmNjUwZGIxYWMyYWFlMTI4NTc3NGM3In0\"\n" +
                    "}\n";

    @Mock
    private ConfiguracionClienteDto configDto;

    @Mock
    private HttpClientUtils httpClientUtils;

    @InjectMocks
    private ClaveUnicaUtils claveUnicaUtils;

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
        InfoCiudadanoDto infoCiudadanoDto = claveUnicaUtils.solicitarInfoCiudadano(new TokenAccesoDto());
        assertThat(infoCiudadanoDto).isEqualToComparingFieldByField(infoEsperado);
    }

    @Test
    public void solicitarTokenAccesoTest() throws ClaveUnicaException {
        TokenAccesoDto tokenEsperado = new TokenAccesoDto();
        tokenEsperado.setAccesToken("95104ab471534af08683aefa7d0935a3");
        tokenEsperado.setTokenType("bearer");
        tokenEsperado.setExpiresIn(3600);
        tokenEsperado.setIdToken("eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg1ZGVjMDU1MjZmNjUwZGIxYWMyYWFlMTI4NTc3NGM3In0");
        HttpResponseDto responseDto = new HttpResponseDto();
        responseDto.setResponseStatus(200);
        responseDto.setResponseByte(TOKEN_ACCESO_JSON.getBytes());
        when(httpClientUtils.doRequest(any(), any(), any(), any())).thenReturn(responseDto);
        when(configDto.getRedirectUrl()).thenReturn("http://www.google.cl/");
        TokenAccesoDto tokenAccesoDto = claveUnicaUtils.solicitarTokenAcceso("cualquiera", "state", "state");
        assertThat(tokenAccesoDto).isEqualToComparingFieldByField(tokenEsperado);
    }


}
