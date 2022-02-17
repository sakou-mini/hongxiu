package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.system.SystemMessageConfigDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import com.donglaistd.jinli.http.entity.LiveUserInfo;
import com.donglaistd.jinli.service.PlatformProcess;
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
@RequestMapping("backOffice/platformQ/")
public class PlatformQController {
    @Autowired
    LiveUserManagerPageProcess liveUserManagerPageProcess;
    @Autowired
    UserManagerPageProcess userManagerPageProcess;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    SystemMessageConfigDaoService systemMessageConfigDaoService;
    @Autowired
    PlatformProcess platformProcess;

    @RequestMapping("/liveRoomListQ")
    @ResponseBody
    public ModelAndView livePlatformRoomList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/liveRoomListQ.html");
    }

    @RequestMapping("/liveUserListQ")
    @ResponseBody
    public ModelAndView liveUserPlatformList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/liveUserListQ.html");
    }

    @RequestMapping("/liveDetailsQ")
    @ResponseBody
    public ModelAndView livePlatformDetails(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/liveDetailsQ.html");
    }

    @RequestMapping("/liveUserDetailsQ")
    @ResponseBody
    public ModelAndView platformLiveUserDetails(HttpServletRequest request, String id) {
        LiveUserInfo liveUserInfo = liveUserManagerPageProcess.getLiveUserApplyDetailInfo(id);
        ModelAndView modelAndView = new ModelAndView("platformQ/liveUserDetailsQ.html");
        modelAndView.addObject("data", liveUserInfo);
        return modelAndView;
    }

    @RequestMapping("/liveUserApplyQ")
    @ResponseBody
    public ModelAndView platformLiveUserApply(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/liveUserApplyQ.html");
    }


    @RequestMapping("/liveUserAuditRecordsQ")
    @ResponseBody
    public ModelAndView platformLiveUserAuditRecords(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/liveUserAuditRecordsQ.html");
    }
    @RequestMapping("/userListQ")
    @ResponseBody
    public ModelAndView platformUserList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/userListQ.html");
    }

    @RequestMapping("/giftConsumptionListQ")
    @ResponseBody
    public ModelAndView platformGiftConsumptionList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/giftConsumptionListQ.html");
    }
    @RequestMapping("/liveUserRecordQ")
    @ResponseBody
    public ModelAndView platformLiveUserRecord(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/liveUserRecordQ.html");
    }
    @RequestMapping("/receivedGiftListQ")
    @ResponseBody
    public ModelAndView platformReceivedGiftList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/receivedGiftListQ.html");
    }
    @RequestMapping("/rechargeRecordQ")
    @ResponseBody
    public ModelAndView platformRechargeRecord(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/rechargeRecordQ.html");
    }

    @RequestMapping("/giftListQ")
    @ResponseBody
    public ModelAndView giftListQ(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/giftListQ.html");
    }

    @RequestMapping("/banLiveUserListQ")
    @ResponseBody
    public ModelAndView platformBanLiveUserList(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("platformQ/banLiveUserListQ.html");
    }


    @RequestMapping("/liveUserDetailInfoQ")
    @ResponseBody
    public Object platformLiveUserDetailInfo(HttpServletRequest request, HttpServletResponse response,String liveUserId) {
        ModelAndView modelAndView = new ModelAndView("platformQ/liveUserDetailInfoQ.html");
        if(liveUserId == null){
            return modelAndView;
        }
        modelAndView.addObject("data", liveUserManagerPageProcess.getLiveUserDetail(liveUserId));
        return modelAndView;
    }

    @RequestMapping("/userDetailsQ")
    @ResponseBody
    public Object platformUserDetails(String userId){

        User user = userDaoService.findById(userId);
        var userData = userManagerPageProcess.getUserListDataByUser(user);
        ModelAndView modelAndView = new ModelAndView("platformQ/userDetailsQ.html");
        modelAndView.addObject("data", userData);
        return modelAndView;
    }



    @RequestMapping("/indexQ")
    @ResponseBody
    public ModelAndView indexQ(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("platformQ/indexQ.html");
        return modelAndView;
    }







}
