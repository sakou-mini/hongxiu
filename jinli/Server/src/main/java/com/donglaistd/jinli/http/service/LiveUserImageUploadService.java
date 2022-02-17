package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.LiveUserApproveStatue;
import com.donglaistd.jinli.database.dao.LiveUserApproveRecordDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.LiveUserApproveRecord;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.donglaistd.jinli.Constant.LiveStatus.UNAPPROVED;
import static com.donglaistd.jinli.Constant.LiveStatus.UNUPLOAD_IMAGE;

@Component
public class LiveUserImageUploadService extends RequestUploadService{
    @Autowired
    DataManager dataManager;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    LiveUserApproveRecordDaoService liveUserApproveRecordDaoService;
    @Override
    public Constant.ResultCode handle(UploadFileInfo uploadFileInfo, User user) {
        LiveUser liveUser = liveUserDaoService.findByUserId(user.getId());
        if(!liveUser.getLiveStatus().equals(UNUPLOAD_IMAGE))
            return Constant.ResultCode.ILLEGAL_OPERATION;
        List<String> files = uploadFileInfo.getFile_path();
        liveUser.setImages(files);
        liveUser.setLiveStatus(UNAPPROVED);
        dataManager.saveLiveUser(liveUser);
        LiveUserApproveRecord approveRecord = liveUserApproveRecordDaoService.findRecentApproveRecordByStatue(liveUser.getId(), LiveUserApproveStatue.UNAPPROVE);
        if(approveRecord == null){
            approveRecord = LiveUserApproveRecord.newInstance(liveUser.getId(),liveUser.getUserId(), liveUser.getPlatformType(), LiveUserApproveStatue.UNAPPROVE);
        }else{
            approveRecord.setApplyDate(System.currentTimeMillis());
        }
        approveRecord.setPlatform(liveUser.getPlatformType());
        liveUserApproveRecordDaoService.save(approveRecord);
        return Constant.ResultCode.SUCCESS;
    }
}
