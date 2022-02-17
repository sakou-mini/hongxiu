package com.donglai.live.entityBuilder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglai.common.util.FileUtil;
import com.donglai.model.db.entity.live.GiftInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GiftBuilder {

    private final Map<String, GiftInfo> giftInfoMap;

    public GiftBuilder(@Value("json/gift.json") final String giftJson) {
        String jsonStr = FileUtil.ReadFile(giftJson);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        log.info("read gift config");
        List<GiftInfo> giftLists = JSON.parseArray(jsonObject.getJSONArray("giftLists").toJSONString(), GiftInfo.class);
        giftInfoMap = giftLists.stream().collect(Collectors.toMap(GiftInfo::getId, giftInfo -> giftInfo));
    }

    public GiftInfo getById(String giftId) {
        return giftInfoMap.get(giftId);
    }
}
