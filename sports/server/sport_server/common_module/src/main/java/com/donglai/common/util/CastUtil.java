package com.donglai.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CastUtil {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        try {
            return (T) obj;
        } catch (Exception e) {
            log.warn("cast failed[]",e.getCause());
            return null;
        }
    }
}
