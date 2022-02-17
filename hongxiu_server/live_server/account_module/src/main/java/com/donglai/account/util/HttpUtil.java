package com.donglai.account.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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

    public static String sendGet(String url, String param) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            //for (String key : map.keySet()) {
            //    System.out.println(key + "--->" + map.get(key));
            //}
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            //System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }


}
