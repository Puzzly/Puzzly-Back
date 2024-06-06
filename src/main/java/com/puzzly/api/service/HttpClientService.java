package com.puzzly.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.api.exception.FailException;
import jakarta.persistence.Basic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityTemplate;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpClientService {
    private final CloseableHttpClient httpClient;

    private final ObjectMapper mapper;

    public Map<String, Object> httpGet(String url, List<NameValuePair> params) throws URISyntaxException {
        Map<String, Object> body = null;
        /** VERIFY REUSING BEAN */
        log.info(Integer.toString(httpClient.hashCode()));

        HttpGet httpGet = new HttpGet(new URIBuilder(url).addParameters(params).build());
        httpGet.addHeader("Content-Type", "application/json; charset=UTF-8");
        try(CloseableHttpResponse response = httpClient.execute(httpGet)){
            String responseBody = EntityUtils.toString(response.getEntity());
            body = mapper.readValue(responseBody, Map.class);

            // TODO when status code != 200
        } catch(Exception e){
            e.printStackTrace();
            throw new FailException(e.getMessage(), 400);
        }
        return body;
    }

    public Map<String, Object> httpPost(String url, List<BasicNameValuePair> params) throws FailException{
        Map<String, Object> body = null;
        /** VERIFY REUSING BEAN */
        log.info(Integer.toString(httpClient.hashCode()));

        HttpPost httpPost = new HttpPost(url);
        JsonNode responseNode = null;

        httpPost.setEntity(new UrlEncodedFormEntity(params));
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        log.error("httpClient HASH : " + httpClient.hashCode());
        try(CloseableHttpResponse response = httpClient.execute(httpPost)){
            String responseBody = EntityUtils.toString(response.getEntity());
            body = mapper.readValue(responseBody, Map.class);

            log.error(responseNode.toPrettyString());
        } catch(Exception e){
            e.printStackTrace();
            throw new FailException(e.getMessage(), 500);
        }
        /*
        EntityTemplate requestEntity =
        ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url)
                .setHeader("Content-Type", "application/json")
                .setCharset(Charset.forName("UTF-8"))
                .setEntity(new UrlEncodedFormEntity(body))
                .build();

         */
        return body;
    }
}
