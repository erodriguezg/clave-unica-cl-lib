package com.github.erodriguezg.http;

import com.github.erodriguezg.http.dto.HttpResponseDto;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpClientUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    public enum RequestMethod {POST, GET}

    public HttpResponseDto doRequest(RequestMethod method, String url, Map<String, String> requestParams, Map<String, String> headers) {

        HttpRequestBase httpRequest;
        if (method.equals(RequestMethod.POST)) {
            httpRequest = doPOST(url, requestParams);
        } else if (method.equals(RequestMethod.GET)) {
            httpRequest = doGET(url, requestParams);
        } else {
            throw new IllegalStateException("metodo http no soportado: " + method);
        }

        loadHeaders(httpRequest, headers);

        try (CloseableHttpClient client = instanceHttpClient()) {
            HttpResponseDto responseDto = new HttpResponseDto();
            HttpResponse response = client.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            log.debug("status: {}", statusCode);
            responseDto.setResponseStatus(statusCode);
            try (InputStream inputStream = response.getEntity().getContent();
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                IOUtils.copy(inputStream, byteArrayOutputStream);
                responseDto.setResponseByte(byteArrayOutputStream.toByteArray());
                return responseDto;
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }


    private HttpRequestBase doPOST(String url, Map<String, String> requestParams) {
        HttpPost httpPost = new HttpPost(url);
        StringBuilder body = new StringBuilder();
        if (requestParams != null && !requestParams.isEmpty()) {
            for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                if (body.length() > 0) {
                    body.append("&");
                }
                body.append(entry.getKey());
                body.append("=");
                body.append(entry.getValue());
            }
        }
        httpPost.setEntity(new StringEntity(body.toString(), ContentType.APPLICATION_FORM_URLENCODED));
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            httpPost.getEntity().writeTo(bos);
            if (log.isDebugEnabled()) {
                log.debug("POST URI: {}", httpPost.getURI());
                log.debug("POST DATA: {}", new String(bos.toByteArray()));
            }
        } catch (IOException e) {
            log.warn("ERROR AL LOGGEAR POST", e);
        }
        return httpPost;
    }

    private HttpRequestBase doGET(String url, Map<String, String> requestParams) {
        URIBuilder builder = null;
        try {
            builder = new URIBuilder(url);
            if (requestParams != null && !requestParams.isEmpty()) {
                for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                    builder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            return new HttpGet(builder.build());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void loadHeaders(HttpRequestBase requestBase, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBase.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private CloseableHttpClient instanceHttpClient() {
        try {
            return HttpClients
                    .custom()
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).build()).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new IllegalStateException(e);
        }
    }


}
