package com.donglaistd.jinli.database.entity.backoffice;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class NewUserReportData {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    @Indexed(unique = true)
    private long date;
    @Field
    private long newUsers;
    @Field
    private long downloads;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long time) {
        this.date = time;
    }

    public long getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(long newUsers) {
        this.newUsers = newUsers;
    }

    public long getDownloads() {
        return downloads;
    }

    public void setDownloads(long downloads) {
        this.downloads = downloads;
    }

    public void addDownload(){
        this.downloads++;
    }

    public NewUserReportData() {
    }

    private NewUserReportData(long date, long newUsers, long downloads) {
        this.date = date;
        this.newUsers = newUsers;
        this.downloads = downloads;
    }

    public static NewUserReportData newInstance(long date, long newUsers, long downloads){
        return new NewUserReportData(date, newUsers, downloads);
    }
}
