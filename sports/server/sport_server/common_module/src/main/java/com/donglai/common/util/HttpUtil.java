package com.donglai.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

@Slf4j
public class HttpUtil {

    public static boolean verifyHostIsAvailable(String url){
        HttpURLConnection oc = null;
        try {
            URL urlObj = new URL(url);
            oc = (HttpURLConnection) urlObj.openConnection();
            oc.setUseCaches(false);
            oc.setConnectTimeout(5000);
            int status = oc.getResponseCode();
            return !verifyCodeIsError(status);
        }  catch (IOException e) {
            log.warn("timeout for domain:" + url);
        }finally {
            if(Objects.nonNull(oc)){
                oc.disconnect();
            }
        }
        return false;
    }

    private static boolean verifyCodeIsError(int status){
        int codeBase = status / 100;
        return codeBase == 4 || codeBase == 5;
    }
}
