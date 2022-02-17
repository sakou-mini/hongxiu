package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.ZoneDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.Zone;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglaistd.jinli.Constant.ResultCode.*;

@Service
public class ZoneImageUploadService extends RequestUploadService{
    @Autowired
    ZoneDaoService zoneDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    VerifyUtil verifyUtil;

    @Override
    public Constant.ResultCode handle(UploadFileInfo uploadFileInfo,User user) {
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if(!verifyUtil.verifyIsLiveUser(liveUser))
            return NOT_LIVE_USER;
        Zone zone = zoneDaoService.findOrCreateZoneByUserId(uploadFileInfo.getUserId());
        if(uploadFileInfo.getFile_path().size()>1)
            return UPLOADPARAM_OVERLIMIT;
        zone.setBackgroundImage(uploadFileInfo.getFile_path().get(0));
        zoneDaoService.save(zone);
        return SUCCESS;
    }
}
