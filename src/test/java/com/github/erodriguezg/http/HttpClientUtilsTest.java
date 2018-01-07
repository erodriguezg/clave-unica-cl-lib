package com.github.erodriguezg.http;

import com.github.erodriguezg.http.dto.HttpResponseDto;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpClientUtilsTest {

    private HttpClientUtils httpClientUtils;

    @Before
    public void before() {
        httpClientUtils = new HttpClientUtils();
    }

    @Test
    public void whenDoGetWithoutParamsAndHeadersThenResponseContentAndCode200() throws UnsupportedEncodingException {
        HttpResponseDto httpResponseDto = httpClientUtils
                .doRequest(HttpClientUtils.RequestMethod.GET, "https://www.google.cl", null, null);
        System.out.println(new String(httpResponseDto.getResponseByte(), "latin1"));
        assertThat(httpResponseDto.getResponseStatus()).isEqualTo(200);
    }

    @Test
    public void whenDoGetWithoutParamsAndHeadersAndUnknownCertificateThenResponseContentAndCode200() throws UnsupportedEncodingException {
        HttpResponseDto httpResponseDto = httpClientUtils
                .doRequest(HttpClientUtils.RequestMethod.GET, "https://sonar6.zeke.cl", null, null);
        System.out.println(new String(httpResponseDto.getResponseByte(), "latin1"));
        assertThat(httpResponseDto.getResponseStatus()).isEqualTo(200);
    }

}
