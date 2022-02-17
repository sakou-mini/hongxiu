package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.RechargeLog;
import com.donglaistd.jinli.http.entity.UserListData;
import com.donglaistd.jinli.service.PlatformProcess;
import com.donglaistd.jinli.service.statistic.LiveUserManagerPageProcess;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backOffice/platform")
public class PlatformCommonController {
    @Autowired
    LiveUserManagerPageProcess liveUserManagerPageProcess;
    @Autowired
    PlatformProcess platformProcess;

    @RequestMapping("/getLiveUserPageList")
    @ResponseBody
    public PageInfo<UserListData> getLiveUserPageList(String liveUserId, String roomId, String userId, int page, int size, int platform){
        return liveUserManagerPageProcess.findPlatformLiveUser(Constant.PlatformType.forNumber(platform),liveUserId, roomId, userId, page-1, size);
    }

    @RequestMapping("/getRechargeRecord")
    @ResponseBody
    public PageInfo<RechargeLog> queryRechargeRecord(int page, int size, Long startTime, Long endTime, String userId, String displayName,int platform) {
        if(endTime!=null) endTime = TimeUtil.getDayEndTime(endTime);
        return platformProcess.queryRechargeRecord(PageRequest.of(page - 1, size),startTime, endTime, userId, displayName,Constant.PlatformType.forNumber(platform));
    }
}
