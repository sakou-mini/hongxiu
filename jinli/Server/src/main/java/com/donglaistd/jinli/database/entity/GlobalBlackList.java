package com.donglaistd.jinli.database.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class GlobalBlackList {
    @Id
    private String userId;
    @Field
    MuteProperty muteProperty;

    private GlobalBlackList(String userId, MuteProperty muteProperty) {
        this.userId = userId;
        this.muteProperty = muteProperty;
    }

    public static GlobalBlackList newInstance(String muteChatUserId,MuteProperty muteProperty){
        return new GlobalBlackList(muteChatUserId, muteProperty);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MuteProperty getMuteProperty() {
        return muteProperty;
    }

    public void setMuteProperty(MuteProperty muteProperty) {
        this.muteProperty = muteProperty;
    }

    public boolean isMute(){
        return getMuteProperty().isMute();
    }

}
