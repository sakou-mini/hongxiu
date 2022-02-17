package com.donglai.live.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HttpUtil {

    public static JSONObject postFormData(Map<String, String> headers, Map<String, Object> param, String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        headers.forEach(httpPost::setHeader);
        List<BasicNameValuePair> pair = param.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue().toString())).collect(Collectors.toList());
        httpPost.setEntity(new UrlEncodedFormEntity(pair, StandardCharsets.UTF_8));
        try {
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            log.info("响应状态：{}", response.getStatusLine());
            log.info("响应内容：{}", responseEntity);
            return JSONObject.parseObject(EntityUtils.toString(responseEntity));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpClient != null) httpClient.close();
                if (response != null) response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONObject getFromUrl(Map<String, String> headers, Map<String, String> param, String url){
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(6000).setConnectTimeout(6000).setConnectionRequestTimeout(6000).build();
        try {
            URIBuilder builder = new URIBuilder(url);
            param.forEach(builder::setParameter);
            HttpGet httpGet = new HttpGet(builder.build());
            headers.forEach(httpGet::setHeader);
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            log.info("响应状态：{}", response.getStatusLine());
            log.info("响应内容：{}", responseEntity);
            return JSONObject.parseObject(EntityUtils.toString(responseEntity));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
