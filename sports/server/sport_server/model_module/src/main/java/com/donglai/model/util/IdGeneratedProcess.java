package com.donglai.model.util;

import com.donglai.common.util.StringNumberUtils;
import com.donglai.model.config.mongodb.MongoDbSaveEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratedProcess {
    private static final String SUFFIX_ID = "_id";
    @Autowired
    MongoDbSaveEventListener mongoDbSaveEventListener;

    public String generatedId(String collection,int length){
        return StringNumberUtils.generateNumberId(mongoDbSaveEventListener.getNextId(collection + SUFFIX_ID), length);
    }
}
