package com.donglai.live.util;

import com.alibaba.fastjson.JSONObject;
import com.donglai.live.BaseTest;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.model.dto.ExtraSportRaceDTO;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class HttpUtilTest extends BaseTest {

    @Test
    public void getTest(){
        Map<String, String> header = new HashMap<>();
        Map<String, String> param = new HashMap<>();
        header.put("Content-Type", "application/vnd.sc-api.v1.json");
        JSONObject result = HttpUtil.getFromUrl(header, param, "https://ap3sport.oxldkm.com/api/sports/livesteam/geteventneeded");
        System.out.println(result);
        if(Objects.nonNull(result)) {
            List<ExtraSportRaceDTO> list = result.getJSONArray("data").toJavaList(ExtraSportRaceDTO.class);
            List<SportEvent> collect = list.stream().map(SportEvent::new).collect(Collectors.toList());
            System.out.println(collect);
        }
    }

    @Test
    public void test(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        System.out.println(format);
        String strTime = "2021-12-28 00:00:00";
        try {
            Date parse = simpleDateFormat.parse(strTime);
            System.out.println(parse.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
