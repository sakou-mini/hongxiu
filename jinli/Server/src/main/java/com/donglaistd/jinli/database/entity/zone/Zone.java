package com.donglaistd.jinli.database.entity.zone;

import com.donglaistd.jinli.Jinli;
import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class Zone {
    @Id
    private ObjectId zoneId = ObjectId.get();
    @Field
    @Indexed(unique = true)
    private String userId;
    @Field
    private String signatureText;
    @Field
    private String backgroundImage;

    private Zone(String userId) {
        this.userId = userId;
    }

    public static Zone getInstance(String userId){
        return new Zone(userId);
    }

    public String getZoneId() {
        return zoneId.toString();
    }

    public String getUserId() {
        return userId;
    }

    public String getBackgroundImage() {
        return Strings.isBlank(backgroundImage) ? "" : backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getSignatureText() {
        return Strings.isBlank(signatureText) ? "" : signatureText;
    }

    public void setSignatureText(String signatureText) {
        this.signatureText = signatureText;
    }

    public Jinli.Zone toProto(){
        return Jinli.Zone.newBuilder()
                .setId(getZoneId())
                .setText(getSignatureText())
                .setBackGroundImage(getBackgroundImage())
                .build();
    }
}
