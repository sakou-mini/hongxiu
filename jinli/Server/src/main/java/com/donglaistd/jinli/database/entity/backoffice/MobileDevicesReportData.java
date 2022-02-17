package com.donglaistd.jinli.database.entity.backoffice;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

public class MobileDevicesReportData {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private long date;
    @Field
    private Map<Constant.MobilePhoneBrand, Integer> newUserBrand = new HashMap<>(0);
    @Field
    private Map<Constant.MobilePhoneBrand,Integer> activeUserBrand =  new HashMap<>(0);
    @Field
    private Map<Constant.MobilePhoneBrand,Integer> payingUserBrand = new HashMap<>(0);
    @Field
    private Map<Constant.MobilePhoneResolution,Integer> payingUserResolution = new HashMap<>(0);
    @Field
    private Map<Constant.MobilePhoneOS,Integer> payingUserOS = new HashMap<>(0);

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

    public Map<Constant.MobilePhoneBrand, Integer> getNewUserBrand() {
        return newUserBrand;
    }

    public void setNewUserBrand(Map<Constant.MobilePhoneBrand, Integer> newUserBrand) {
        this.newUserBrand = newUserBrand;
    }

    public Map<Constant.MobilePhoneBrand, Integer> getActiveUserBrand() {
        return activeUserBrand;
    }

    public void setActiveUserBrand(Map<Constant.MobilePhoneBrand, Integer> activeUserBrand) {
        this.activeUserBrand = activeUserBrand;
    }

    public Map<Constant.MobilePhoneBrand, Integer> getPayingUserBrand() {
        return payingUserBrand;
    }

    public void setPayingUserBrand(Map<Constant.MobilePhoneBrand, Integer> payingUserBrand) {
        this.payingUserBrand = payingUserBrand;
    }

    public Map<Constant.MobilePhoneResolution, Integer> getPayingUserResolution() {
        return payingUserResolution;
    }

    public void setPayingUserResolution(Map<Constant.MobilePhoneResolution, Integer> payingUserResolution) {
        this.payingUserResolution = payingUserResolution;
    }

    public Map<Constant.MobilePhoneOS, Integer> getPayingUserOS() {
        return payingUserOS;
    }

    public void setPayingUserOS(Map<Constant.MobilePhoneOS, Integer> payingUserOS) {
        this.payingUserOS = payingUserOS;
    }

    public MobileDevicesReportData() {
    }

    private MobileDevicesReportData(long date) {
        this.date = date;
    }

    public static MobileDevicesReportData newInstance(long date){
        return new MobileDevicesReportData(date);
    }

    public void addNewUserBrandNumByPhoneBrand(Constant.MobilePhoneBrand brand,int num){
        newUserBrand.put(brand, newUserBrand.getOrDefault(brand, 0) + num);
    }

    public void addActiveUserBrandNumByPhoneBrand(Constant.MobilePhoneBrand brand,int num){
        activeUserBrand.put(brand, newUserBrand.getOrDefault(brand, 0) + num);
    }

    public void addPayingUserBrandNumByPhoneBrand(Constant.MobilePhoneBrand brand,int num){
        payingUserBrand.put(brand, newUserBrand.getOrDefault(brand, 0) + num);
    }
}
