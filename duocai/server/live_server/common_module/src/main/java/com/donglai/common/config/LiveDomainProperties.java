package com.donglai.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.donglai.common.constant.LineSourceConstant.*;

@Component
@Data
public class LiveDomainProperties {

    @Value("${wangsu.live.nodes}")
    private List<String> wangSuDomains;

    @Value("${tencent.live.nodes}")
    private List<String> tencentDomains;

    @Value("${aliyun.live.nodes}")
    private List<String> aliyunDomains;

    public List<String> getDefaultDomainListByLine(int lineCode){
        switch (lineCode){
            case WANGSU_LINE:
                return wangSuDomains;
            case TENCENT_LINE:
                return tencentDomains;
            case ALI_LINE:
                return aliyunDomains;
            default:
                return new ArrayList<>();
        }
    }
}
