package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.DiaryResourceDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.dao.ZoneDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.DiaryResource;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class ZoneBaseTest extends BaseTest {
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    DiaryResourceDaoService diaryResourceDaoService;

    public PersonDiary createPassDiary(User user){
        PersonDiary personDiary = PersonDiary.newInstance(user.getId(), "image", Constant.DiaryType.IMAGE,2);
        List<DiaryResource> resourceList = new ArrayList<>();
        resourceList.add(DiaryResource.newInstance(personDiary.getId(),""+1));
        resourceList.add(DiaryResource.newInstance(personDiary.getId(),""+2));
        diaryResourceDaoService.saveAllResource(resourceList);
        personDiary.setStatue(Constant.DiaryStatue.DIARY_APPROVAL_PASS);
        personDiaryDaoService.save(personDiary);
        return personDiary;
    }

    public void initDiaryData(int num, User user){
        for (int i = 0; i <num ; i++) {
            PersonDiary personDiary = PersonDiary.newInstance(user.getId(), "image"+i, Constant.DiaryType.IMAGE,2);
            List<DiaryResource> resourceList = new ArrayList<>();
            resourceList.add(DiaryResource.newInstance(personDiary.getId(),""+1));
            resourceList.add(DiaryResource.newInstance(personDiary.getId(),""+2));
            diaryResourceDaoService.saveAllResource(resourceList);
            personDiary.setStatue(Constant.DiaryStatue.DIARY_APPROVAL_PASS);
            personDiaryDaoService.save(personDiary);
        }
    }

    public PersonDiary createPersonDiary(User user, Constant.DiaryType type,String content,Constant.DiaryStatue statue){
        PersonDiary personDiary = PersonDiary.newInstance(user.getId(), content, type,1);
        diaryResourceDaoService.saveDiaryResource(DiaryResource.newInstance(personDiary.getId(), "" + 1));
        personDiary.setStatue(statue);
        personDiaryDaoService.save(personDiary);
        return personDiary;
    }
}
