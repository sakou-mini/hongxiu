package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import org.bson.Document;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoDbExplaneDemo extends BaseTest {
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void test(){
        Query query = new Query(Criteria.where("follower").is("5fa251fcfc32b3266681a704"));
        Document explainDocument = new Document();
        explainDocument.put("find", "followList");
        explainDocument.put("filter", query.getQueryObject());

        Document command = new Document();
        command.put("explain", explainDocument);

        Document explainResult = mongoTemplate.getDb().runCommand(command);
        System.out.println(explainResult.toJson());
    }

}
