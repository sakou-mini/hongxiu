package com.donglai.web.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Moon
 * @date 2021-11-11 11:42
 */
@Slf4j
public class JsonUtil {
    public static JSONObject getJsonMap(String fileName) {
        String json = "";
        try {
            File jsonFile = null;
            jsonFile = ResourceUtils.getFile(fileName);
            json = FileUtils.readFileToString(jsonFile);
        } catch (FileNotFoundException e) {
            log.error("文件解析失败,错误:{}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.parseObject(json);
    }

    public static JSONArray getJsonJSONArray(String fileName) {
        String json = "";
        try {
            File jsonFile = null;
            jsonFile = ResourceUtils.getFile(fileName);
            json = FileUtils.readFileToString(jsonFile);
        } catch (FileNotFoundException e) {
            log.error("文件解析失败,错误:{}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.parseArray(json);
    }
}
