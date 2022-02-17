/*
package com.donglai.live.config.mongodb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class SecondaryMongoConfig extends AbstractMongoConfig{

    public static final String MONGO_TEMPLATE = "secondaryMongoTemplate";

    @Value("${spring.data.secondary.mongodb.uri}")
    public String secondaryMongoDbUrl;

    @Override
    public MongoDatabaseFactory mongoDatabaseFactory(){
        return new SimpleMongoClientDatabaseFactory(secondaryMongoDbUrl);
    }

    @Bean(name = MONGO_TEMPLATE)
    @Override
    public MongoTemplate getMongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory());
    }
}
*/
