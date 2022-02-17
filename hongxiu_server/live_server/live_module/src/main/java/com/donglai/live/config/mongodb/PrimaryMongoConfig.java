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
public class PrimaryMongoConfig extends AbstractMongoConfig{
    public static final String MONGO_TEMPLATE = "primaryMongoTemplate";

    @Value("${spring.data.primary.mongodb.uri}")
    public String primaryMongoDbUrl;

    @Override
    public MongoDatabaseFactory mongoDatabaseFactory(){
        return new SimpleMongoClientDatabaseFactory(primaryMongoDbUrl);
    }

    @Primary
    @Bean(name = MONGO_TEMPLATE)
    @Override
    public MongoTemplate getMongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory());
    }
}
*/
