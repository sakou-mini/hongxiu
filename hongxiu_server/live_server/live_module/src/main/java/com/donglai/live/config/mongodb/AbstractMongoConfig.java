package com.donglai.live.config.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

@Data
public abstract class AbstractMongoConfig {
    public String mongodbUri;

    public abstract MongoDatabaseFactory mongoDatabaseFactory();

    public abstract MongoTemplate getMongoTemplate();
}
