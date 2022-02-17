package com.donglai.model.db.entity.common;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

@Document(collection = "numberIdInfo")
@Data
public class NumberIdInfo {
    @Id
    private String id;

    @Field
    @Indexed(unique = true)
    private String collName;

    @Field
    private Long incId;
}
