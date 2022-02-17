package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;

import java.util.Date;
import java.util.List;

public class PersonDiaryListSend {
    public String id;
    public String userId;
    public String displayName;
    public String content;
    public Constant.DiaryType type;
    public Constant.DiaryStatue state;
    public List<String> resourceUrlLis;
    public String backOfficeName;
    public long uploadTime;
    public Date operationDate;
    public String rejectContent;
    public boolean recommend;
}
