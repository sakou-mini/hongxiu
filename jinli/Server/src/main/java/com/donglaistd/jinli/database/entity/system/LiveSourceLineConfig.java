package com.donglaistd.jinli.database.entity.system;

import com.donglaistd.jinli.Constant;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Document
public class LiveSourceLineConfig {
    @Id
    private String id;
    @Field
    private Set<Constant.LiveSourceLine> availableLine;
    @Field
    private Constant.PlatformType platformType;


    public LiveSourceLineConfig() {
    }

    public LiveSourceLineConfig(Constant.PlatformType platformType) {
        this.id = String.valueOf(platformType.getNumber());
        this.platformType = platformType;
        this.availableLine = initLiveSource();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Constant.LiveSourceLine> getAvailableLine() {
        return availableLine;
    }

    public void setAvailableLine(Set<Constant.LiveSourceLine> availableLine) {
        this.availableLine = availableLine;
    }

    public boolean liveSourceLineIsAvailable(Constant.LiveSourceLine liveSourceLine){
        return availableLine.contains(liveSourceLine);
    }

    public Constant.PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }

    private Set<Constant.LiveSourceLine> initLiveSource(){
        return Arrays.stream(Constant.LiveSourceLine.values()).filter(liveSourceLine -> !liveSourceLine.equals(Constant.LiveSourceLine.UNRECOGNIZED)).collect(Collectors.toSet());
    }

}
