package com.donglai.model.db.entity.common;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.common.constant.ServerStatus;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

import static com.donglai.common.constant.CommonConstant.SERVER_ID;
import static com.donglai.common.constant.ServerStatus.RUNNING;

@Data
@Document
public class ServerProperty {
    @Id
    @AutoIncKey
    private long id = SERVER_ID;
    @Field
    private ServerStatus serverStatus = RUNNING;
    @Field
    private long updateTime = System.currentTimeMillis();

    public static ServerProperty newInstance(){
        return new ServerProperty();
    }
}
