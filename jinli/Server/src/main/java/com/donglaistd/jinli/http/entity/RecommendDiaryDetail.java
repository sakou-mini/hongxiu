package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;

import java.util.ArrayList;
import java.util.List;

public class RecommendDiaryDetail {
    public String id;
    public List<Integer> unavailablePositions = new ArrayList<>();
    public int position;
    public Constant.DiaryRecommendTimeType recommendTime;
    public boolean recommend;
    public int recommendSize;
}
