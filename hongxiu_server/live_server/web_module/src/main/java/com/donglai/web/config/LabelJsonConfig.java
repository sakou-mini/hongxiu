package com.donglai.web.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.donglai.web.util.JsonUtil;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @author Moon
 * @date 2021-11-22 11:18
 */
@Data
@Configuration
public class LabelJsonConfig {
    /**
     * 所有标签
     */
    private JSONArray labelsArray;
    /**
     * 人物标签
     */
    private JSONObject characterBreakdown;
    /**
     * 行为标签
     */
    private JSONObject characterBehavior;


    public LabelJsonConfig() {
        this.labelsArray = JsonUtil.getJsonJSONArray("json/allLabels.json");
        this.characterBreakdown = JsonUtil.getJsonMap("json/characterBreakdown.json");
        this.characterBehavior = JsonUtil.getJsonMap("json/characterBehavior.json");
    }
}
