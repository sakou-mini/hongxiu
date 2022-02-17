package com.donglaistd.jinli.database.entity.backoffice;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class ActiveUserReportData {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private long date;
    @Field
    private int newUserDau;
    @Field
    private int oldUserDau;
    @Field
    private int wau;
    @Field
    private int mau;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getNewUserDau() {
        return newUserDau;
    }

    public void setNewUserDau(int newUserDau) {
        this.newUserDau = newUserDau;
    }

    public int getOldUserDau() {
        return oldUserDau;
    }

    public void setOldUserDau(int oldUserDau) {
        this.oldUserDau = oldUserDau;
    }

    public int getWau() {
        return wau;
    }

    public void setWau(int wau) {
        this.wau = wau;
    }

    public int getMau() {
        return mau;
    }

    public void setMau(int mau) {
        this.mau = mau;
    }

    public ActiveUserReportData() {
    }

    private ActiveUserReportData(long date, int newUserDau, int oldUserDau, int wau, int mau) {
        this.date = date;
        this.newUserDau = newUserDau;
        this.oldUserDau = oldUserDau;
        this.wau = wau;
        this.mau = mau;
    }

    public static ActiveUserReportData newInstance(long date, int newUserDau, int oldUserDau, int wau, int mau){
        return new ActiveUserReportData(date, newUserDau, oldUserDau, wau, mau);
    }
}
