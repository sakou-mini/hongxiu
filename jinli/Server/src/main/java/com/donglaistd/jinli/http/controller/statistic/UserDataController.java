package com.donglaistd.jinli.http.controller.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.statistic.DayLiveDataTotal;
import com.donglaistd.jinli.http.dto.request.PlatformTimeRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.service.statistic.UserManagerPageProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("backOffice/userData")
public class UserDataController {
    @Autowired
    UserManagerPageProcess userManagerPageProcess;

    @RequestMapping("/onlineDataSummary")
    public Object getOnlineDataSummary() {
        return userManagerPageProcess.getOnlineUserDataSummary();
    }

    @RequestMapping("/currentMonthDataSummary")
    public Object geCurrentMonthDataSummary() {
        return userManagerPageProcess.getCurrentMonthUserDataSummary();
    }

    @RequestMapping("/todayUserDataSummary")
    public Object getTodayUserDataSummary() {
        return userManagerPageProcess.getTodayUserDataSummary();
    }

    @RequestMapping("/monthUserDataDetail")
    public Object getMonthUserDataDetail() {
        return userManagerPageProcess.getMonthUserDetail();
    }

    @RequestMapping("/todayUserDataDetail")
    public Object getTodayUserDataDetail(int page, int size) {
        return userManagerPageProcess.getTodayUserDataDetail(size, page);
    }


    //Other platform IndexData
    @RequestMapping("/platformIndexData")
    public Object getPlatformIndexData(int platform) {
        return userManagerPageProcess.getPlatformIndexData(Constant.PlatformType.forNumber(platform));
    }

    /*今日直播数据汇总*/
    @RequestMapping("todayLiveTotalData")
    public DayLiveDataTotal todayLiveTotalData(int platform) {
        return userManagerPageProcess.getPlatformTodayLiveData(Constant.PlatformType.forNumber(platform));
    }

    /*每日直播数据汇总查询*/
    @RequestMapping("queryLiveTotalData")
    public PageInfo<DayLiveDataTotal> todayLiveTotalData(PlatformTimeRequest request) {
        request.setPage(request.getPage()-1);
        return userManagerPageProcess.getPlatformLiveDataByPlatformTimeRequest(request);
    }

    /*用户数据汇总*/
    @RequestMapping("queryUserTotalData")
    public Object queryUserTotalData(){
        return null;
    }
}
