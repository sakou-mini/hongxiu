package com.donglaistd.jinli.service;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.dao.RecommendDiaryDaoService;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.database.entity.zone.RecommendDiary;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class DiaryProcessTest extends BaseTest {
    @Autowired
    DiaryProcess diaryProcess;
    @Autowired
    RecommendDiaryDaoService recommendDiaryDaoService;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Test
    public void randomTest(){
        RecommendDiary hotDiary1 = RecommendDiary.newInstance("1", 1, Constant.DiaryRecommendTimeType.RECOMMEND_SIX_HOUR);
        recommendDiaryDaoService.save(hotDiary1);
        hotDiary1 = RecommendDiary.newInstance("2", 2, Constant.DiaryRecommendTimeType.RECOMMEND_SIX_HOUR);
        recommendDiaryDaoService.save(hotDiary1);
        hotDiary1 = RecommendDiary.newInstance("3", 3, Constant.DiaryRecommendTimeType.RECOMMEND_SIX_HOUR);
        recommendDiaryDaoService.save(hotDiary1);
        PersonDiary diary1 = PersonDiary.newInstance(user.getId(), "jjj", Constant.DiaryType.IMAGE, 1);
        diary1.setStatue(Constant.DiaryStatue.DIARY_APPROVAL_PASS);
        PersonDiary diary2 = PersonDiary.newInstance(user.getId(), "jjj", Constant.DiaryType.IMAGE, 1);
        diary2.setStatue(Constant.DiaryStatue.DIARY_APPROVAL_PASS);
        PersonDiary diary3 = PersonDiary.newInstance(user.getId(), "jjj", Constant.DiaryType.IMAGE, 1);
        diary3.setStatue(Constant.DiaryStatue.DIARY_APPROVAL_PASS);
        PersonDiary diary4 = PersonDiary.newInstance(user.getId(), "jjj", Constant.DiaryType.IMAGE, 1);
        diary4.setStatue(Constant.DiaryStatue.DIARY_APPROVAL_PASS);
        personDiaryDaoService.save(diary1);
        personDiaryDaoService.save(diary2);
        personDiaryDaoService.save(diary3);
        personDiaryDaoService.save(diary4);


        Set<String> result = diaryProcess.randomDiary("张三丰", 2);
        Assert.assertEquals(2,result.size());
        Assert.assertTrue(result.containsAll(Lists.newArrayList("1","2")));


        result = diaryProcess.randomDiary("张三丰", 2);
        Assert.assertEquals(2,result.size());
        Assert.assertTrue(result.contains("3"));


        result = diaryProcess.randomDiary("张三丰", 2);
        Assert.assertEquals(2,result.size());

        result = diaryProcess.randomDiary("张三丰", 5);
        Assert.assertEquals(5,result.size());
        //
        result = diaryProcess.randomDiary("张三丰", 3);
        Assert.assertEquals(3,result.size());   //because  filter viewDiary ,only left 2 diary
    }
}
