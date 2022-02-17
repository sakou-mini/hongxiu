package com.donglaistd.jinli.other;

import com.donglaistd.jinli.config.GiftConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class JsonCheckTest {
    @Test
    public void checkAllJsonConfigFiles() throws IOException {
        File schemaFile = new File("config/json/schema.json");
        Assert.assertTrue(schemaFile.exists());
        JSONObject rawSchema = new JSONObject(new JSONTokener(new FileInputStream(schemaFile)));
        Schema schema = SchemaLoader.load(rawSchema);
        var file = new File("config/json/gift.json");
        Assert.assertTrue(file.exists());
        JSONObject jsonObject = new JSONObject(new JSONTokener(new FileInputStream(file)));
        schema.validate(jsonObject);
    }

    @Test
    public void checkInitGiftConfigFromJson() throws IOException {
        var jsonObject = new JSONObject(new JSONTokener(new FileInputStream(new File("config/json/gift.json"))));
        var configs = new ObjectMapper().readValue(jsonObject.getJSONArray("giftLists").toString(), GiftConfig[].class);
        Assert.assertEquals(1, configs[0].price);
        Assert.assertEquals("10000", configs[0].id);
        Assert.assertEquals(100, configs[1].price);
        Assert.assertEquals("10008", configs[1].id);
    }

    @Test
    public void checkTaskJsonConfigFiles() throws IOException {
        File schemaFile = new File("config/json/schema.json");
        Assert.assertTrue(schemaFile.exists());
        JSONObject rawSchema = new JSONObject(new JSONTokener(new FileInputStream(schemaFile)));
        Schema schema = SchemaLoader.load(rawSchema);
        var file = new File("config/json/task.json");
        Assert.assertTrue(file.exists());
        JSONObject jsonObject = new JSONObject(new JSONTokener(new FileInputStream(file)));
        schema.validate(jsonObject);
    }
}
