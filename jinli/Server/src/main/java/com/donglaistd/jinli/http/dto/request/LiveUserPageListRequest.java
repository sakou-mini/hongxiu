package com.donglaistd.jinli.http.dto.request;

import com.donglaistd.jinli.Constant;

import java.util.Objects;

public class LiveUserPageListRequest {
    private Integer platform;
    private String condition;
    private int page;
    private int size;
    /*0 query all pass statue  query ,1 query normal statue ,2 query ban liveUser*/
    private int statue;

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public Constant.PlatformType getPlatformType(){
        if(Objects.isNull(platform)) return null;
        return Constant.PlatformType.forNumber(platform);
    }

    public QueryLiveUserStatue getQueryStatue(){
        return QueryLiveUserStatue.valueOf(statue);
    }

    public enum QueryLiveUserStatue {
        ALL(0),
        NORMAL(1),
        BAN(2);

        private final int code;

        QueryLiveUserStatue(int code) {
            this.code = code;
        }

        public static QueryLiveUserStatue valueOf(int typeValue){
            switch (typeValue){
                case 0: return ALL;
                case 1: return NORMAL;
                case 2: return BAN;
                default: return null;
            }
        }

        public int getCode() {
            return code;
        }
    }
}
