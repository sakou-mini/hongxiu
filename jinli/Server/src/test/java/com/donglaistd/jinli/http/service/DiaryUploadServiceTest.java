package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.DiaryResourceDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.DiaryResource;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.DiaryStatue.DIARY_UNAPPROVED;

public class DiaryUploadServiceTest extends BaseTest {
    Logger logger = Logger.getLogger(DiaryUploadServiceTest.class.getName());
    @Autowired
    PersonDiaryDaoService diaryDaoService;
    @Autowired
    DiaryUploadService diaryUploadService;
    @Autowired
    DiaryResourceDaoService diaryResourceDaoService;

    public PersonDiary createPassDiary(User user){
        PersonDiary personDiary = PersonDiary.newInstance(user.getId(), "image", Constant.DiaryType.IMAGE,8);
        List<DiaryResource> resourceList = new ArrayList<>();
        personDiary.setStatue(Constant.DiaryStatue.DIARY_UPLOADING);
        diaryDaoService.save(personDiary);
        return personDiary;
    }

    public ExecutorService createThreadPool()
    {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(2, 10, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Test
    public void uploadDiaryThreadTest(){
        ExecutorService threadPool = createThreadPool();
        PersonDiary diary = createPassDiary(user); //default 8 resource
        diaryDaoService.save(diary);
        for (int i = 0; i < 8; i++) {
            UploadFileInfo uploadInfo = createUploadInfo(diary.getId(), "image" + i);
            threadPool.execute(()->diaryUploadService.handle(uploadInfo, user));
        }
        threadPool.shutdown();
        while (true){
            if(threadPool.isTerminated()){
                logger.info("uploadFinish!");
                long resourceNum = diaryResourceDaoService.countDiaryResource(diary.getId());
                Assert.assertEquals(8,resourceNum);
                diary = diaryDaoService.findById(diary.getId());
                Assert .assertEquals(DIARY_UNAPPROVED,diary.getStatue());
                break;
            }
        }
    }

    public UploadFileInfo createUploadInfo(String diaryId,String resource){
        UploadFileInfo uploadFileInfo = new UploadFileInfo();
        uploadFileInfo.getExamParam().put("diaryId", new String[]{diaryId});
        uploadFileInfo.getFile_path().add(resource);
        return uploadFileInfo;
    }
}
