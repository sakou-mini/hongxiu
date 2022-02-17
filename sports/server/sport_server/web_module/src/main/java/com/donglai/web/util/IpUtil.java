package com.donglai.web.util;

import com.donglai.common.util.StringUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class IpUtil {

    public static String getIP(FullHttpRequest req){
        if(req == null) return null;
        log.info("CF-Connecting-IP is"+req.headers().get("CF-Connecting-IP"));
        log.info("x-forwarded-for is"+req.headers().get("x-forwarded-for"));
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
}
