package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.backoffice.PersonDiaryOperationlog;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.database.entity.zone.RecommendDiary;
import com.donglaistd.jinli.http.dto.request.DiaryListRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.PersonDiaryListSend;
import com.donglaistd.jinli.http.service.BackOfficeUserService;
import com.donglaistd.jinli.service.DiaryProcess;
import com.donglaistd.jinli.util.TimeUtil;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static com.donglaistd.jinli.Constant.DiaryStatue.*;

public class PersonDiaryDaoServiceTest extends BaseTest {
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    PersonDiaryOperationLogDaoService personDiaryOperationLogDaoService;
    @Autowired
    RecommendDiaryDaoService recommendDiaryDaoService;
    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    DiaryProcess diaryProcess;

    @Test
    public void createPersonDiaryTest(){
        PersonDiary diary1 = PersonDiary.newInstance(user.getId(), "55sdffgg", Constant.DiaryType.IMAGE,9);
        diary1.setStatue(DIARY_APPROVED_FAIL);
        PersonDiary diary2 = PersonDiary.newInstance(user.getId(), "1231sddg", Constant.DiaryType.IMAGE,9);
        PersonDiary diary3 = PersonDiary.newInstance(user.getId(), "12312sdf", Constant.DiaryType.IMAGE,9);
        diary3.setStatue(DIARY_UNAPPROVED);
        PersonDiary diary4 = PersonDiary.newInstance(user.getId(), "123123sasd", Constant.DiaryType.IMAGE,9);
        diary4.setStatue(DIARY_APPROVED_FAIL);
        PersonDiary diary5 = PersonDiary.newInstance(user.getId(), "撒大苏打", Constant.DiaryType.VIDEO,9);
        diary5.setStatue(DIARY_APPROVAL_PASS);
        personDiaryDaoService.save(diary1);
        personDiaryDaoService.save(diary2);
        personDiaryDaoService.save(diary3);
        personDiaryDaoService.save(diary4);
        personDiaryDaoService.save(diary5);
        List<PersonDiary> diaryListAll = personDiaryDaoService.findUserNotFailDiary(user.getId());
        Assert.assertEquals(2, diaryListAll.size());
    }

    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;

    @Test
    public void getDiaryListTest(){

        BackOfficeUser backOfficeUser = backOfficeUserService.createBackOfficeUser("accountTest", "pwd", Lists.newArrayList());

        PersonDiary diary;
        for (int i = 0; i < 10; i++) {
            diary = PersonDiary.newInstance(user.getId(), "审核通过"+i, Constant.DiaryType.IMAGE,9);
            diary.setStatue(DIARY_APPROVAL_PASS);
            PersonDiaryOperationlog personDiaryOperationlog = new PersonDiaryOperationlog();
            personDiaryOperationlog.setBackOfficeId(backOfficeUser.getId());
            personDiaryOperationlog.setOperationDate(new Date());
            personDiaryOperationlog.setApproval(true);
            personDiaryOperationlog.setPersonDiaryId(diary.getId());
            personDiaryDaoService.save(diary);
            personDiaryOperationLogDaoService.save(personDiaryOperationlog);
            if(i >=0 && i<3){
                RecommendDiary recommendDiary = RecommendDiary.newInstance(diary.getId(), i + 1, Constant.DiaryRecommendTimeType.RECOMMEND_SIX_HOUR);
                recommendDiaryDaoService.save(recommendDiary);
            }
        }

        for (int i = 0; i < 6; i++) {
            diary = PersonDiary.newInstance(user.getId(), "审核不通过"+i, Constant.DiaryType.IMAGE,9);
            diary.setStatue(DIARY_APPROVED_FAIL);
            PersonDiaryOperationlog personDiaryOperationlog = new PersonDiaryOperationlog();
            personDiaryOperationlog.setBackOfficeId(backOfficeUser.getId());
            personDiaryOperationlog.setOperationDate(new Date());
            personDiaryOperationlog.setApproval(false);
            personDiaryOperationlog.setPersonDiaryId(diary.getId());
            personDiaryDaoService.save(diary);
            personDiaryOperationLogDaoService.save(personDiaryOperationlog);
        }

        for (int i = 0; i < 5; i++) {
            diary = PersonDiary.newInstance(user.getId(), "未审核"+i, Constant.DiaryType.IMAGE,9);
            diary.setStatue(DIARY_UNAPPROVED);
            personDiaryDaoService.save(diary);
        }

        // 10 个 审核通过且 3 个被推荐  ，6个 审核不通过 ，5个未审核
        /*查询所有动态*/
        DiaryListRequest request = new DiaryListRequest("",
                TimeUtil.getCurrentDayStartTime(),
                System.currentTimeMillis(),
                0,
                0,
                0,
                30);
        PageInfo<PersonDiaryListSend> diaryList = diaryProcess.findDiaryList(request);
        Assert.assertEquals(21,diaryList.getTotal());
        Assert.assertEquals(21,diaryList.getContent().size());

        /*查询未审核*/
        request = new DiaryListRequest("",
                TimeUtil.getCurrentDayStartTime(),
                System.currentTimeMillis(),
                1,
                0,
                0,
                30);
        diaryList = diaryProcess.findDiaryList(request);
        Assert.assertEquals(5,diaryList.getTotal());
        Assert.assertEquals(5,diaryList.getContent().size());

        /*查询所有，但推荐得动态*/
        request = new DiaryListRequest("",
                TimeUtil.getCurrentDayStartTime(),
                System.currentTimeMillis(),
                0,
                1,
                0,
                2);
        diaryList = diaryProcess.findDiaryList(request);
        Assert.assertEquals(3,diaryList.getTotal());
        Assert.assertEquals(2,diaryList.getContent().size());

        /*查询审核未通过*/
        request = new DiaryListRequest("",
                TimeUtil.getCurrentDayStartTime(),
                System.currentTimeMillis(),
                3,
                0,
                0,
                10);
        diaryList = diaryProcess.findDiaryList(request);
        Assert.assertEquals(6,diaryList.getTotal());
        Assert.assertEquals(6,diaryList.getContent().size());
    }

}
