package com.donglai.gate.util;

import com.donglai.common.util.StringUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketHttpUtil {

    public static String getIP(FullHttpRequest req) {
        if(req == null) return null;
        log.info("X-Real-IP is"+req.headers().get("X-Real-IP"));
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

        ip = splitIp.length > 1 ? splitIp[0] : ip;
        log.info("获取到的ip地址:{}" ,ip);
        return ip;
    }
}
