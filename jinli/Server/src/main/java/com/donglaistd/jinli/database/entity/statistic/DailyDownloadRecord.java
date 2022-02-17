package com.donglaistd.jinli.database.entity.statistic;

import com.donglaistd.jinli.util.TimeUtil;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

@Document
public class DailyDownloadRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private long downloadCount;
    @Field
    @Indexed(unique = true)
    private long time;

    public DailyDownloadRecord() {
        this.time = TimeUtil.getCurrentDayStartTime();
    }

    public DailyDownloadRecord(long downloadCount, long time) {
        this.downloadCount = downloadCount;
        this.time = TimeUtil.getCurrentDayStartTime();
    }

    public static DailyDownloadRecord newInstance(long downloadCount, long time){
        return new DailyDownloadRecord(downloadCount, time);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(long downloadCount) {
        this.downloadCount = downloadCount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void addDownloadCount(){
        this.downloadCount++;
    }
}
