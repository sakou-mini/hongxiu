package com.donglaistd.jinli.config;

import com.donglaistd.jinli.annotation.AutoIncKey;
import com.donglaistd.jinli.database.entity.IncInfo;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.logging.Logger;

@Component
public class SaveEventListener extends AbstractMongoEventListener<Object> {
    private static final Logger logger= Logger.getLogger(SaveEventListener.class.getName());

    private final MongoTemplate mongo;
    private final MongoOperations mongoOperations;

    public SaveEventListener(MongoTemplate mongo, MongoOperations mongoOperations) {
        this.mongo = mongo;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        if (source != null) {
            ReflectionUtils.doWithFields(source.getClass(), field -> {
                ReflectionUtils.makeAccessible(field);
                if (field.isAnnotationPresent(AutoIncKey.class) && field.getLong(source) <= 0) {
                    field.set(source, getNextId(source.getClass().getSimpleName()));
                }
            });
        }
    }

    public Integer getNextId(String collName) {
        Query query = new Query(Criteria.where("collName").is(collName));
        Update update = new Update();
        update.inc("incId", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        IncInfo inc= mongo.findAndModify(query, update, options, IncInfo.class);
        assert inc != null;
        return inc.getIncId();
    }

    public void initCollId(String collName,int startNum){
        Query query = new Query(Criteria.where("collName").is(collName));
        if(mongo.findOne(query,IncInfo.class) == null) {
            UpdateResult result = mongoOperations.upsert(query, new Update().set("incId", startNum), IncInfo.class);
        }
    }
}