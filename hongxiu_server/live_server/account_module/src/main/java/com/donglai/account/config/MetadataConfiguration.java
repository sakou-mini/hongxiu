package com.donglai.account.config;

import com.donglai.proto.metadata.Metadata;
import com.googlecode.protobuf.format.JsonFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class MetadataConfiguration {

    @Bean
    public Metadata.Table initMetadata() throws IOException {
        Metadata.Table.Builder builder = Metadata.Table.newBuilder();
        new JsonFormat().merge(new FileInputStream(new File("json/metadata.json")), builder);
        return builder.build();
    }
}
