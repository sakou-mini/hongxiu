package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.DiaryReplyDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.component.UserSummaryInfo;
import com.donglaistd.jinli.database.entity.zone.DiaryReply;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ReplyTest extends BaseTest {
    @Autowired
    DiaryReplyDaoService replyDaoService;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    UserDaoService userDaoService;


    @Test
    public void replyDiaryTest(){
        PersonDiary diary = PersonDiary.newInstance(user.getId(), "中秋节快到了！", Constant.DiaryType.IMAGE,9);
        personDiaryDaoService.save(diary);
        String diaryId = diary.getId();

        //comment1
        User zhangSan = createTester(100, "张三");
        userDaoService.save(zhangSan);
        DiaryReply reply1 = DiaryReply.newInstance(diaryId, "提前祝你节日快乐!", UserSummaryInfo.newInstance(zhangSan));
        replyDaoService.save(reply1);

        //comment2
        User liSi = createTester(100, "李四");
        userDaoService.save(liSi);
        DiaryReply reply2 = DiaryReply.newInstance(diaryId, "到时候一起去赏月啊！", UserSummaryInfo.newInstance(liSi));
        replyDaoService.save(reply2);

        reply2 = replyDaoService.findById(reply2.getId());
        DiaryReply reply3 = DiaryReply.newInstance(diaryId, "可以呀！", UserSummaryInfo.newInstance(user));
        reply3.setLastReply(reply2.getId(),UserSummaryInfo.newInstance(liSi));
        reply2.addReplyNum();
        replyDaoService.save(reply3);


        User liFeng = createTester(20, "李峰");
        userDaoService.save(liFeng);
        DiaryReply reply4 = DiaryReply.newInstance(diaryId, "我也想去", UserSummaryInfo.newInstance(liFeng));
        reply4.setLastReply(reply2.getId(),UserSummaryInfo.newInstance(zhangSan));
        reply2.addReplyNum();
        replyDaoService.save(reply4);
        replyDaoService.save(reply2);
        List<DiaryReply> rootReply = replyDaoService.findPageRootReply(diaryId,0,10,null);
        Assert.assertEquals(2,rootReply.size());

        List<DiaryReply> replyList = replyDaoService.findAllSecondReply(reply2.getId(),null);
        Assert.assertEquals(2,replyList.size());
    }

}

