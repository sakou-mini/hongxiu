package com.donglaistd.jinli.http.controller;


import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.system.SystemMessageConfigDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import com.donglaistd.jinli.http.entity.LiveUserInfo;
import com.donglaistd.jinli.service.statistic.LiveUserManagerPageProcess;
import com.donglaistd.jinli.service.statistic.UserManagerPageProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("backOffice/platformT/")
public class PlatformTController {

    @Autowired
    LiveUserManagerPageProcess liveUserManagerPageProcess;
    @Autowired
    UserManagerPageProcess userManagerPageProcess;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    SystemMessageConfigDaoService systemMessageConfigDaoService;

    @RequestMapping("/liveRoomListT")
    @ResponseBody
    public ModelAndView livePlatformRoomList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/liveRoomListT.html");
    }

    @RequestMapping("/liveUserListT")
    @ResponseBody
    public ModelAndView liveUserPlatformList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/liveUserListT.html");
    }

    @RequestMapping("/liveDetailsT")
    @ResponseBody
    public ModelAndView livePlatformDetails(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/liveDetailsT.html");
    }

    @RequestMapping("/liveUserDetailsT")
    @ResponseBody
    public ModelAndView platformLiveUserDetails(HttpServletRequest request, String id) {
        LiveUserInfo liveUserInfo = liveUserManagerPageProcess.getLiveUserApplyDetailInfo(id);
        ModelAndView modelAndView = new ModelAndView("platformT/liveUserDetailsT.html");
        modelAndView.addObject("data", liveUserInfo);
        return modelAndView;
    }

    @RequestMapping("/liveUserApplyT")
    @ResponseBody
    public ModelAndView platformLiveUserApply(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/liveUserApplyT.html");
    }


    @RequestMapping("/liveUserAuditRecordsT")
    @ResponseBody
    public ModelAndView platformLiveUserAuditRecords(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/liveUserAuditRecordsT.html");
    }
    @RequestMapping("/userListT")
    @ResponseBody
    public ModelAndView platformUserList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/userListT.html");
    }

    @RequestMapping("/giftListT")
    @ResponseBody
    public ModelAndView giftListT(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/giftListT.html");
    }

    @RequestMapping("/giftConsumptionListT")
    @ResponseBody
    public ModelAndView platformGiftConsumptionList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/giftConsumptionListT.html");
    }
    @RequestMapping("/liveUserRecordT")
    @ResponseBody
    public ModelAndView platformLiveUserRecord(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/liveUserRecordT.html");
    }
    @RequestMapping("/receivedGiftListT")
    @ResponseBody
    public ModelAndView platformReceivedGiftList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/receivedGiftListT.html");
    }
    @RequestMapping("/rechargeRecordT")
    @ResponseBody
    public ModelAndView platformRechargeRecord(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/rechargeRecordT.html");
    }

    @RequestMapping("/banLiveUserListT")
    @ResponseBody
    public ModelAndView platformBanLiveUserList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/banLiveUserListT.html");
    }



    @RequestMapping("/domainNameSetT")
    @ResponseBody
    public ModelAndView domainNameSetT(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/domainNameSetT.html");
        return modelAndView;
    }

    @RequestMapping("/domainNameSetRecordT")
    @ResponseBody
    public ModelAndView domainNameSetRecordT(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("backOfficeManagement/domainNameSetRecordT.html");
        return modelAndView;
    }

    @RequestMapping("/indexT")
    @ResponseBody
    public ModelAndView indexT(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("platformT/indexT.html");
        return modelAndView;
    }

    @RequestMapping("/liveUserDetailInfoT")
    @ResponseBody
    public Object platformLiveUserDetailInfo(HttpServletRequest request, HttpServletResponse response,String liveUserId) {
        ModelAndView modelAndView = new ModelAndView("platformT/liveUserDetailInfoT.html");
        if(liveUserId == null){
            return modelAndView;
        }
        modelAndView.addObject("data", liveUserManagerPageProcess.getLiveUserDetail(liveUserId));
        return modelAndView;
    }

    @RequestMapping("/userDetailsT")
    @ResponseBody
    public Object platformUserDetails(String userId){

        User user = userDaoService.findById(userId);
        var userData = userManagerPageProcess.getUserListDataByUser(user);
        ModelAndView modelAndView = new ModelAndView("platformT/userDetailsT.html");
        modelAndView.addObject("data", userData);
        return modelAndView;
    }

    @RequestMapping("/backOfficeMessageT")
    @ResponseBody
    public ModelAndView sendBackOfficeMessage(HttpServletRequest request, HttpServletResponse response) {
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(Constant.PlatformType.PLATFORM_T);
        ModelAndView modelAndView = new ModelAndView("platformT/backOfficeMessageT.html");
        if(systemMessageConfig != null){
            modelAndView.addObject("message", systemMessageConfig.getRollMessage());
        }
        return modelAndView;
    }

    @RequestMapping("/messageSettingsT")
    @ResponseBody
    public ModelAndView platformMessageSettings(HttpServletRequest request, HttpServletResponse response) {
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(Constant.PlatformType.PLATFORM_T);
        ModelAndView modelAndView =  new ModelAndView("platformT/messageSettingsT.html");
        modelAndView.addObject("message", systemMessageConfig.getSystemTipMessage());
        return modelAndView;
    }


    @RequestMapping("/backOfficeMessageRecordT")
    @ResponseBody
    public ModelAndView backOfficeMessageRecordT(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/backOfficeMessageRecordT.html");
    }

    @RequestMapping("/messageSettingsRecordT")
    @ResponseBody
    public ModelAndView messageSettingsRecordT(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformT/messageSettingsRecordT.html");
    }
}
