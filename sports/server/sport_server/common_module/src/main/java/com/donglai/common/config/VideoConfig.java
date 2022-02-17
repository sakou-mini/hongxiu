package com.donglai.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Moon
 * @date 2021-11-10 17:11
 */
@Component
@Data
public class VideoConfig {

    @Value("${tupu.secretId}")
    private String secretId;

    @Value("${tupu.privateKey}")
    private String privateKey;

    @Value("${tupu.callbackUrl}")
    private String callbackUrl;
}
