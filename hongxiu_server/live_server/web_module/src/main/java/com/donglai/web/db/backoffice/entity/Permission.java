package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document
@NoArgsConstructor
public class Permission {
    @Id
    @AutoIncKey
    private String id;
    private String name;
    private String auth;
    private long createTime;
    private long updateTime;
    private String operator_id;
}
