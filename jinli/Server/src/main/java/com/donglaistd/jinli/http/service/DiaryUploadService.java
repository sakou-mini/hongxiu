package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.DiaryResourceDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.DiaryResource;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import com.donglaistd.jinli.util.DataManager;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.DiaryStatue.DIARY_UNAPPROVED;
import static com.donglaistd.jinli.Constant.DiaryStatue.DIARY_UPLOADING;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.Constant.UploadImageDescribe.THUMBNAIL_VALUE;
import static com.donglaistd.jinli.constant.WebKeyConstant.DIARYID;

@Component
public class DiaryUploadService extends RequestUploadService{
    private static final Logger logger = Logger.getLogger(DiaryUploadService.class.getName());

    @Autowired
    PersonDiaryDaoService diaryDaoService;
    @Autowired
    DiaryResourceDaoService diaryResourceDaoService;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Override
    public Constant.ResultCode handle(UploadFileInfo uploadFileInfo, User user) {
        String diaryId = uploadFileInfo.getExamParam(DIARYID)[0];
        if(Strings.isBlank(diaryId)) {
            logger.info("diaryId is null");
            return PARAM_ERROR;
        }
        PersonDiary diary = diaryDaoService.findById(diaryId);
        if(Objects.isNull(diary) || !Objects.equals(DIARY_UPLOADING,diary.getStatue()) ) return DIARY_NOTFOUND;
        return dealUploadDiary(diary, uploadFileInfo,user);
    }

    public Constant.ResultCode dealUploadDiary(PersonDiary diary, UploadFileInfo uploadFileInfo, User user){
        synchronized (DataManager.getUserLock(user.getId())){
            String diaryId = diary.getId();
            if(uploadFileInfo.getUploadImageDescribe() == THUMBNAIL_VALUE){
                diary.setThumbnailUrl(uploadFileInfo.getFile_path().get(0));
                diaryDaoService.save(diary);
            }else{
                if(!verifyDiaryUploadNum(diary,uploadFileInfo.getFile_path().size())) {
                    logger.warning("UPLOAD IS OVERLIMIT");
                    return UPLOADPARAM_OVERLIMIT;
                }
                List<DiaryResource> resources = new ArrayList<>();
                for (String resource : uploadFileInfo.getFile_path()) {
                    resources.add(DiaryResource.newInstance(diaryId, resource));
                }
                diaryResourceDaoService.saveAllResource(resources);
                if(diaryResourceDaoService.countDiaryResource(diaryId)>=diary.getTargetNum()){
                    logger.info("UPLOAD DIARY FINISH！------------------------->"+diaryId + "--->SUCCESS!");
                    diary.setStatue(DIARY_UNAPPROVED);
                    DataManager.removeUserLock(user.getId());
                    diaryDaoService.save(diary);
                }
            }
            return SUCCESS;
        }
    }

    private boolean verifyDiaryUploadNum(PersonDiary personDiary,int uploadNum){
        long sourceNum = diaryResourceDaoService.countDiaryResource(personDiary.getId());
        return sourceNum + uploadNum <= personDiary.getTargetNum();
    }
}
