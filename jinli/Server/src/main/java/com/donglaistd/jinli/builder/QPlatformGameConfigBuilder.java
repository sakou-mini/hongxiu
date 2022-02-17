package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.config.QPlatformGameConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QPlatformGameConfigBuilder {
    private static final String KEY_SPLIT = "_";
    Map<String, QPlatformGameConfig> qPlatformGameConfigMap = new HashMap<>();

    public QPlatformGameConfigBuilder(@Value("${jinli.config.qplatformGame.config.file.path}") final String configFilePath) throws FileNotFoundException, JsonProcessingException {
        File file = new File(configFilePath);
        assert file.exists();
        var jsonObject = new JSONObject(new JSONTokener(new FileInputStream(file)));
        var configs = new ObjectMapper().readValue(jsonObject.getJSONArray("gameList").toString(), QPlatformGameConfig[].class);
        for (QPlatformGameConfig gameConfig : configs) {
            qPlatformGameConfigMap.put(gameConfig.getGameCode() + KEY_SPLIT + gameConfig.getRoomCode(), gameConfig);
        }
    }

    public QPlatformGameConfig getPlatformConfig(String gameCode,String roomCode) {
        String key = gameCode + KEY_SPLIT + roomCode;
        return qPlatformGameConfigMap.get(key);
    }

    public List<QPlatformGameConfig> getAllQplatformGames(){
        return new ArrayList<>(qPlatformGameConfigMap.values());
    }
}
