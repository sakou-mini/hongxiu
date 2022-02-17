package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.entity.component.UserSummaryInfo;
import com.donglaistd.jinli.database.entity.zone.DiaryReply;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DiaryReplyDaoServiceTest extends BaseTest {
    @Autowired
    DiaryReplyDaoService diaryReplyDaoService;

    @Test
    public void rootDiaryReplyPageQueryTest(){
        long start = System.currentTimeMillis();
        String diaryId = "日志1";
        //模拟根回复
        List<DiaryReply> rootReply = createDiaryReply(diaryId);
        //模拟子回复
        DiaryReply root1 = rootReply.get(0);
        createChildDiaryReply(diaryId, root1.getId());
        int totalNum = diaryReplyDaoService.queryRootReplyNum(diaryId);
        List<String> excludeIds = rootReply.subList(0, 3).stream().map(DiaryReply::getId).collect(Collectors.toList());
        Assert.assertEquals(1000,totalNum);

        List<DiaryReply> pageRootReply = diaryReplyDaoService.findPageRootReply(diaryId, 0, 10, excludeIds);
        Assert.assertEquals(10,pageRootReply.size());

        List<DiaryReply> pageRootReply2 = diaryReplyDaoService.findPageRootReply(diaryId, 0, 1000, excludeIds);
        Assert.assertEquals(997,pageRootReply2.size());
    }

    public List<DiaryReply> createDiaryReply(String diaryId){
        List<DiaryReply> rootReplyList = new ArrayList<>();
        for (int i = 0; i <1000 ; i++) {
            DiaryReply reply = DiaryReply.newInstance(diaryId, "模拟评论" + i, UserSummaryInfo.newInstance(user));
            rootReplyList.add(diaryReplyDaoService.save(reply));
        }
        return rootReplyList;
    }

    public void  createChildDiaryReply(String diaryId,String replyId){
        for (int i = 0; i <1000 ; i++) {
            DiaryReply reply = DiaryReply.newInstance(diaryId, "模拟评论" + i, UserSummaryInfo.newInstance(user));
            reply.setLastReply(replyId,UserSummaryInfo.newInstance(user));
            diaryReplyDaoService.save(reply);
        }
    }
}
