package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.system.GiftConfig;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_DEFAULT;

@Service
public class GiftConfigDaoService {
    private final Logger logger = Logger.getLogger(GiftConfigDaoService.class.getName());

    private final GiftConfigRepository giftConfigRepository;
    private final MongoTemplate mongoTemplate;

    public GiftConfigDaoService(GiftConfigRepository giftConfigRepository, MongoTemplate mongoTemplate) {
        this.giftConfigRepository = giftConfigRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<GiftConfig> saveAll(List<GiftConfig> giftConfigs) {
        return giftConfigRepository.saveAll(giftConfigs);
    }

    public GiftConfig save(GiftConfig giftConfig){
        return giftConfigRepository.save(giftConfig);
    }

    public List<GiftConfig> findAllByPlatform(Constant.PlatformType platformType){
        return giftConfigRepository.findByPlatformType(platformType);
    }

    public GiftConfig findByGiftIdAndPlatform(String giftId, Constant.PlatformType platformType){
        return giftConfigRepository.findByGiftIdAndPlatformType(giftId,platformType);
    }

    public void resetGift() {
        giftConfigRepository.deleteAll();
        initGiftConfig();
    }

    public void initGiftConfig() {
        giftConfigRepository.deleteByPlatformTypeIsNull();
        List<GiftConfig> giftConfigList;
        for (Constant.PlatformType platformType : Constant.PlatformType.values()) {
            if (Objects.equals(PLATFORM_DEFAULT, platformType) || Objects.equals(Constant.PlatformType.UNRECOGNIZED, platformType)) continue;
            giftConfigList = giftConfigRepository.findByPlatformType(platformType);
            if (giftConfigList.isEmpty()) {
                try {
                    var file = new File("config/json/gift.json");
                    JSONObject jsonObject = new JSONObject(new JSONTokener(new FileInputStream(file)));
                    var configs = new ObjectMapper().readValue(jsonObject.getJSONArray("giftLists").toString(), GiftConfig[].class);
                    for (GiftConfig config : configs) {
                        config.setPlatformType(platformType);
                    }
                    saveAll(Lists.newArrayList(configs));
                    logger.info("Save giftConfig to DB success! by Platform" + platformType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JSONObject getFullGiftConfigForJSon(Constant.PlatformType platform){
        try {
            List<GiftConfig> allGift = findAllByPlatform(platform);
            var file = new File("config/json/gift.json");
            JSONObject jsonObject = new JSONObject(new JSONTokener(new FileInputStream(file)));
            jsonObject.put("giftLists", allGift);
            return jsonObject;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PageInfo<GiftConfig> giftConfigPageInfo(boolean luxury, PageRequest pageRequest, Constant.PlatformType platform) {
        Criteria criteria = Criteria.where("luxury").is(luxury).and("platformType").is(platform);
        Query query = Query.query(criteria);
        long totalNum = mongoTemplate.count(query, GiftConfig.class);
        List<GiftConfig> content = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.ASC, "giftId")), GiftConfig.class);
        return new PageInfo<>(content,totalNum);
    }
}
