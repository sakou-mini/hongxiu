package com.donglai.live.meta;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglai.common.util.FileUtil;
import com.donglai.model.db.entity.meta.LineSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LineSourceMetaBuilder {

    private final Map<Integer, LineSource> lineSourceMap;

    public LineSourceMetaBuilder(@Value("json/LiveLine.json") final String lineJson) {
        String jsonStr = FileUtil.ReadFile(lineJson);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        List<LineSource> giftLists = JSON.parseArray(jsonObject.getJSONArray("lines").toJSONString(), LineSource.class);
        lineSourceMap = giftLists.stream().collect(Collectors.toMap(LineSource::getId, lineSource -> lineSource));
    }

    public LineSource getById(Integer id) {
        return lineSourceMap.get(id);
    }
}
