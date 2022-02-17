package com.donglai.live.process;

import com.alibaba.fastjson.JSONObject;
import com.donglai.live.util.HttpUtil;
import com.donglai.model.dto.ExtraSportRaceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class SportRaceProcess {

    @Value("${sport.race.url}")
    public String raceUrl;

    public List<ExtraSportRaceDTO> getSportRaceList(){
        Map<String, String> header = new HashMap<>();
        Map<String, String> param = new HashMap<>();
        header.put("Content-Type", "application/vnd.sc-api.v1.json");
        JSONObject result = HttpUtil.getFromUrl(header, param, raceUrl);
        if(Objects.isNull(result)) return null;
        return result.getJSONArray("data").toJavaList(ExtraSportRaceDTO.class);
    }
}
