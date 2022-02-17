package com.donglaistd.jinli.config;

import com.donglaistd.jinli.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LiveDomainDefaultConfig {

    @Value("${wangsu.live.nodes}")
    private List<String> wangSuDomains;

    @Value("${tencent.live.nodes}")
    private List<String> tencentDomains;

    @Value("${aliyun.live.nodes}")
    private List<String> aliyunDomains;

    public List<String> getWangSuDomains() {
        return wangSuDomains;
    }

    public void setWangSuDomains(List<String> wangSuDomains) {
        this.wangSuDomains = wangSuDomains;
    }

    public List<String> getTencentDomains() {
        return tencentDomains;
    }

    public void setTencentDomains(List<String> tencentDomains) {
        this.tencentDomains = tencentDomains;
    }

    public List<String> getAliyunDomains() {
        return aliyunDomains;
    }

    public void setAliyunDomains(List<String> aliyunDomains) {
        this.aliyunDomains = aliyunDomains;
    }

    public List<String> getDefaultDomainListByLine(Constant.LiveSourceLine line){
        switch (line){
            case WANGSU_LINE:
                return wangSuDomains;
            case ALIYUN_LINE:
                return aliyunDomains;
            case TENCENT_LINE:
                return tencentDomains;
            default:
                return new ArrayList<>();
        }
    }
}
