package com.donglai.model.db.entity.account;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
@NoArgsConstructor
public class UserFeedback {
    @Id
    @AutoIncKey
    private long id;
    @Field
    private String userId;
    @Field
    private String content;
    @Field
    private List<String> pictures = new ArrayList<>();
    @Field
    private long createTime;
    @Field
    private String appVersion;
    @Field
    private String mobileModel;

    public UserFeedback(String userId, String content, List<String> pictures, String appVersion, String mobileModel) {
        this.userId = userId;
        this.content = content;
        this.pictures = pictures;
        this.appVersion = appVersion;
        this.mobileModel = mobileModel;
        this.createTime = System.currentTimeMillis();
    }

    public static UserFeedback newInstance(String userId, String content, List<String> pictures, String appVersion, String mobileMobileModel) {
        return new UserFeedback(userId, content, pictures, appVersion, mobileMobileModel);
    }
}
