package com.donglai.model.config.mongodb;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.model.db.entity.common.NumberIdInfo;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Objects;

@Component
@Slf4j
public class MongoDbSaveEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MongoOperations mongoOperations;


    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            // 如果字段添加了我们自定义的AutoIncKey注解
            if (field.isAnnotationPresent(AutoIncKey.class)) {
                long filedValue = field.get(source) == null ? 0 : Long.parseLong(field.get(source).toString());
                if (filedValue > 0) return;
                Class<?> type = field.getType();
                if (type.equals(String.class)) {
                    field.set(source, getNextId(source.getClass().getName()).toString());
                }else if(Objects.equals(type.getName(),"int") || type.equals(Integer.class)) {
                    field.set(source, Integer.valueOf(getNextId(source.getClass().getName()).toString()));
                }else {
                    field.set(source, getNextId(source.getClass().getName()));
                }
            }
        });
    }

    public Long getNextId(String collName) {
        Query query = new Query(Criteria.where("collName").is(collName));
        Update update = new Update();
        update.inc("incId", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        NumberIdInfo inc = mongoTemplate.findAndModify(query, update, options, NumberIdInfo.class);
        assert inc != null;
        return inc.getIncId();
    }

    public void initCollId(String collName, int startNum) {
        Query query = new Query(Criteria.where("collName").is(collName));
        if (mongoTemplate.findOne(query, NumberIdInfo.class) == null) {
            UpdateResult result = mongoOperations.upsert(query, new Update().set("incId", startNum), NumberIdInfo.class);
        }
    }
}