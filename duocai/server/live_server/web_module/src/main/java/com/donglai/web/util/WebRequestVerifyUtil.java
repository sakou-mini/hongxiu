package com.donglai.web.util;

public class WebRequestVerifyUtil {

    public static boolean verifyPageRequest(int page,int size){
        return page >= 0 && size > 0;
    }
}
