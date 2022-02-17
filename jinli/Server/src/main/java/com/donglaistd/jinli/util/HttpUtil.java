package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.http.entity.GiftSendInfo;
import com.donglaistd.jinli.http.entity.RechargeResult;
import com.google.gson.Gson;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.constant.GameConstant.RESTFUL_RESULT_SUCCESS;

@Component
public class HttpUtil {

    private final static Logger logger = Logger.getLogger(HttpUtil.class.getName());
    @Autowired
    private RestTemplate restTemplate;

    @Value("${platformQ.recharge.url}")
    private String platformQRechargeUrl;

    public static String getIP(HttpServletRequest request){
        String ip=request.getHeader("x-forwarded-for");
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("Proxy-Client-IP");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("X-Real-IP");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getRemoteAddr();
        }
        return ip;
    }

    public static String getIP(FullHttpRequest req){
        if(req == null) return null;
        logger.info("CF-Connecting-IP is"+req.headers().get("CF-Connecting-IP"));
        logger.info("x-forwarded-for is"+req.headers().get("x-forwarded-for"));
        String ip=req.headers().get("CF-Connecting-IP");
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=req.headers().get("x-forwarded-for");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=req.headers().get("Proxy-Client-IP");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=req.headers().get("WL-Proxy-Client-IP");
        }
        if(StringUtils.isNullOrBlank(ip)) return null;
        String[] splitIp = ip.split(",");
        if(splitIp.length > 1) return splitIp[0];
        else return ip;
    }

    //getRequest
    public <T> ResponseEntity<T> getFromUrl(String url,Class<T> responseType){
       return restTemplate.getForEntity(url, responseType);
    }

    public <T> ResponseEntity<T> getFromUrlForEntity(String url,Class<T> responseType,Map<String, Object> params){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if(Objects.nonNull(params)){
            params.forEach(builder::queryParam);
        }
        url = builder.build().toString();
        return restTemplate.getForEntity(url, responseType);
    }

    public BufferedImage getImageInfoFromURL(String url){
        url = "http://" + url;
        ResponseEntity<Resource> response = getFromUrl(url, Resource.class);
        try {
            InputStream inputStream = response.getBody().getInputStream();
           return ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //post
    public <T> ResponseEntity<T> httpPostFrom(String url, Map<String, Object> params, Map<String, String> headers, Class<T> clazz) {
        HttpHeaders httpHeaders = new HttpHeaders();
        params = params == null ? new LinkedHashMap<>() : params;
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        MultiValueMap<String, Object> stringObjectLinkedMultiValueMap = new LinkedMultiValueMap<>();
        params.forEach(stringObjectLinkedMultiValueMap::add);
        return restTemplate.postForEntity(url, new HttpEntity(stringObjectLinkedMultiValueMap, httpHeaders), clazz);
    }

    public Constant.ResultCode verifyImageSize(String avatar, String remoteAddress, int limitWidth, int limitHeight){
        BufferedImage image = getImageInfoFromURL(remoteAddress + avatar);
        if(image==null) return UNKNOWN;
        if((image.getWidth() != limitWidth || image.getHeight() != limitHeight))
            return DATA_FORMAT_MALFORMED;
        return SUCCESS;
    }

    public boolean checkHostIsLive(String remoteAddress){
        try {
            Socket socket = new Socket();
            String[] address = remoteAddress.split(":");
            socket.connect(new InetSocketAddress(address[0], Integer.parseInt(address[1])), 2000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyHostIsAvailable(String url){
        HttpURLConnection oc = null;
        try {
            URL urlObj = new URL(url);
            oc = (HttpURLConnection) urlObj.openConnection();
            oc.setUseCaches(false);
            oc.setConnectTimeout(5000);
            int status = oc.getResponseCode();
            HttpStatus httpStatus = HttpStatus.valueOf(status);
            return !httpStatus.isError();
        }  catch (IOException e) {
            logger.warning("timeout for domain:" + url);
        }finally {
            if(Objects.nonNull(oc)){
                oc.disconnect();
            }
        }
        return false;
    }

    public boolean requestRewardForPlatformQ (String accountName, String gameCode, long amount, GiftSendInfo giftInfo,String orderId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", accountName);
            params.put("game_code", gameCode);
            params.put("amount", amount);
            params.put("reward_msg", giftInfo);
            params.put("order_id", orderId);
            logger.info("url is:"+platformQRechargeUrl+ "   request param is:"+params);
            ResponseEntity<String> responseEntity = postJsonData(params,platformQRechargeUrl,String.class);
            logger.info("扣费响应信息为："+responseEntity.getBody());
            RechargeResult rechargeResult = new Gson().fromJson(responseEntity.getBody(), RechargeResult.class);
            return Objects.equals(rechargeResult.getResult(), RESTFUL_RESULT_SUCCESS);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public <T> ResponseEntity<T> postJsonData(Map<String,Object> param,String url,Class<T> responseType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        MediaType type=MediaType.parseMediaType("application/json;charset=UTF-8");
        httpHeaders.setContentType(type);
        HttpEntity<Map<String, Object>> objectHttpEntity = new HttpEntity<>(param,httpHeaders);
        return restTemplate.postForEntity(url, objectHttpEntity, responseType);
    }

}
