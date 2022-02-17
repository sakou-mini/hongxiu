package com.donglaistd.jinli.http.dto.request;

import com.donglaistd.jinli.Constant;

import java.util.Objects;

public class LiveUserApproveRecordRequest {
    public int page;
    public int limit;
    public int queryType;
    public String condition;
    public int platform;
    public int queryStatue;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public int getQueryStatue() {
        return queryStatue;
    }

    public void setQueryStatue(int queryStatue) {
        this.queryStatue = queryStatue;
    }

    public Constant.PlatformType getPlatformType(){
        if(Objects.isNull(platform)) return null;
        return Constant.PlatformType.forNumber(platform);
    }

    public StatueType getStatueType(){
        return StatueType.valueOf(queryStatue);
    }


    public enum StatueType {
        ALL(0),
        UNAPPROVE(1),
        PASS(2),
        NO_PASS(3);

        private final int code;

        StatueType(int code) {
            this.code = code;
        }

        public static StatueType valueOf(int typeValue){
            switch (typeValue){
                case 0: return ALL;
                case 1: return UNAPPROVE;
                case 2: return PASS;
                case 3: return NO_PASS;
                default: return null;
            }
        }

        public int getCode() {
            return code;
        }
    }
}
