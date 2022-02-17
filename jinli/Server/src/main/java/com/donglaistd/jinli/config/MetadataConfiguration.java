package com.donglaistd.jinli.config;

import com.donglaistd.jinli.metadata.Metadata;
import com.googlecode.protobuf.format.JsonFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class MetadataConfiguration {
    @Bean
    public Metadata.Table initMetadata() throws IOException {
        Metadata.Table.Builder builder = Metadata.Table.newBuilder();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("metadata/json/metadata.json");
        new JsonFormat().merge(inputStream, builder);
        return builder.build();
    }
}
