package com.donglaistd.jinli.http.controller;


import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.LiveUserBuilder;
import com.donglaistd.jinli.builder.RoomBuilder;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.constant.GameConstant;
import com.donglaistd.jinli.constant.LiveUserApproveStatue;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.dao.statistic.LiveSourceLineConfigDaoService;
import com.donglaistd.jinli.database.entity.LiveLimitRecord;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.accusation.Report;
import com.donglaistd.jinli.database.entity.backoffice.LiveUserApproveRecord;
import com.donglaistd.jinli.database.entity.statistic.DailyLiveRecordInfo;
import com.donglaistd.jinli.database.entity.system.LiveSourceLineConfig;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.http.dto.reply.LiveLimitListReply;
import com.donglaistd.jinli.http.dto.request.*;
import com.donglaistd.jinli.http.entity.*;
import com.donglaistd.jinli.http.entity.live.LiveAttendance;
import com.donglaistd.jinli.http.entity.live.LiveRecordInfo;
import com.donglaistd.jinli.http.response.GenericResponse;
import com.donglaistd.jinli.http.response.SuccessResponse;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.LiveLimitProcess;
import com.donglaistd.jinli.service.statistic.LiveUserManagerPageProcess;
import com.donglaistd.jinli.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;


@Controller
@RequestMapping("backOffice/liveUser/")
public class LiveUserController {
    private static final Logger logger = Logger.getLogger(LiveUserController.class.getName());

    @Autowired
    DataManager dataManager;
    @Autowired
    private LiveUserDaoService liveUserDaoService;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private LiveUserApproveRecordDaoService liveUserApproveRecord;
    @Autowired
    private BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    CoinFlowDaoService coinFlowDaoService;
    @Autowired
    CoinFlowService coinFlowService;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    RoomBuilder roomBuilder;
    @Autowired
    LiveUserManagerPageProcess liveUserManagerPageProcess;
    @Autowired
    private ReportDaoService reportDaoService;
    @Autowired
    LiveSourceLineConfigDaoService liveSourceLineConfigDaoService;
    @Autowired
    LiveLimitProcess liveLimitProcess;

    @RequestMapping("/list_unapproved_live_users")
    @ResponseBody
    public Object listUnapprovedLiveUsers(HttpServletRequest request, HttpServletResponse response) {
        var users = liveUserDaoService.findLiveUsersByStatus(Constant.LiveStatus.UNAPPROVED);
        for (var liveUser : users) {
            var user = userDaoService.findById(liveUser.getUserId());
        }
        ModelAndView modelAndView = new ModelAndView("unapproved_live_users.html");

        modelAndView.addObject("unapprovedLiveUsers", users);
        return modelAndView;
    }

    @RequestMapping("/approve_live_user")
    @ResponseBody
    public Object approveLiveUser(HttpServletRequest request, HttpServletResponse response, String userId) {
        logger.fine("userid" + userId);
        var liveUser = liveUserDaoService.findById(userId);
        if (liveUser == null) {
            return "failed";
        }
        liveUser.setLiveStatus(Constant.LiveStatus.OFFLINE);
        dataManager.saveLiveUser(liveUser);
        return new ModelAndView("unapproved_live_users.html");
    }

    @RequestMapping("/refusal_live_user")
    @ResponseBody
    public Object refusalLiveUser(HttpServletRequest request, HttpServletResponse response, String userId) {
        logger.fine("userid" + userId);
        var liveUser = liveUserDaoService.findById(userId);
        if (liveUser == null) {
            return "failed";
        }
        liveUser.setLiveStatus(Constant.LiveStatus.APPROVED_FAIL);
        dataManager.saveLiveUser(liveUser);
        return new ModelAndView("unapproved_live_users.html");
    }

    @RequestMapping("/LiveUserExamine")
    @ResponseBody
    public Object LiveUserExamine(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/LiveUserExamine.html");
        modelAndView.addObject("message", "success");
        return modelAndView;
    }

    @RequestMapping("/liveDetails")
    @ResponseBody
    public Object liveDetails(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveDetails.html");
        return modelAndView;
    }

    @RequestMapping("/liveUserRecord")
    @ResponseBody
    public Object liveUserRecord(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveUserRecord.html");
        return modelAndView;
    }

    @RequestMapping("/rejectLiveUser")
    @ResponseBody
    public Object rejectLiveUser(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/rejectLiveUser.html");
        modelAndView.addObject("message", "success");
        return modelAndView;
    }

    @RequestMapping("/approvedLiveUser")
    @ResponseBody
    public Object approvedLiveUser(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/approvedLiveUser.html");
        modelAndView.addObject("message", "success");
        return modelAndView;
    }

    @RequestMapping("/liveUserList")
    @ResponseBody
    public Object liveUserList(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveUserList.html");
        modelAndView.addObject("message", "success");
        return modelAndView;
    }

    @RequestMapping("/liveRecord")
    @ResponseBody
    public Object liveRecord(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveRecord.html");
        return modelAndView;
    }

    @RequestMapping("/liveUserAttendance")
    @ResponseBody
    public Object liveUserAttendance(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveUserAttendance.html");
        return modelAndView;
    }

    @RequestMapping("/livePlatformDetails")
    @ResponseBody
    public Object livePlatformDetails(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/livePlatformDetails.html");
        modelAndView.addObject("message", "success");
        return modelAndView;
    }

    @RequestMapping("/liveUserDetails")
    @ResponseBody
    public Object liveUserDetails(HttpServletRequest request, HttpServletResponse response, String id) {
        LiveUserInfo liveUserInfo = liveUserManagerPageProcess.getLiveUserApplyDetailInfo(id);
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveUserDetails.html");
        modelAndView.addObject("data", liveUserInfo);
        return modelAndView;
    }

    @RequestMapping("/liveSourceLine")
    @ResponseBody
    public Object liveSourceLine() {
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveSourceLine.html");
        return modelAndView;
    }


    @RequestMapping("/banLiveUserList")
    @ResponseBody
    public Object banLiveUserList() {
        return new ModelAndView("LiveUser/banLiveUserList.html");
    }

    @RequestMapping("/liveUserClockOn")
    @ResponseBody
    public Object liveUserClockOn() {
        return new ModelAndView("LiveUser/liveUserClockOn.html");
    }

   /* @RequestMapping("/getAppLiveUserList")
    @ResponseBody
    public Object getAppLiveUserList(int page, int limit, int type, String msg, int platform) {
        PageInfo<LiveUser> applyLiveUserPageInfo = liveUserManagerPageProcess.getApplyLiveUserPageInfo(page - 1, limit, type, msg, platform);
        return liveUserDataInfo(applyLiveUserPageInfo);
    }*/

    @RequestMapping("/getAppLiveUserList")
    @ResponseBody
    public GenericResponse getAppLiveUserList(LiveUserApproveRecordRequest request) {
        PageInfo<ApplicationLiveUserData> pageInfo = liveUserManagerPageProcess.getLiveUserApproveRecordPageInfo(request);
        return new SuccessResponse().withData(pageInfo);
    }
/*
    public HttpURLConnection<ApplicationLiveUserData> liveUserDataInfo(PageInfo<LiveUser> getLiveUserPage) {
        var sendData = new HttpURLConnection<ApplicationLiveUserData>();
        var getLiveUserList = new ArrayList<>(getLiveUserPage.getContent());
        for (int i = 0; i < getLiveUserList.size(); i++) {
            if (getLiveUserList.get(i).getLiveStatus() != Constant.LiveStatus.UNAPPROVED) {
                continue;
            }
            var dataList = new ApplicationLiveUserData();
            String id = getLiveUserList.get(i).getUserId();
            var user = userDaoService.findById(id);
            dataList.getGameCoin = user.getGameCoin();
            dataList.displayName = user.getDisplayName();
            dataList.avatarUrl = user.getAvatarUrl();
            if (user.getPhoneNumber() == null) {
                dataList.phoneNumber = "unbound";
            } else {
                dataList.phoneNumber = user.getPhoneNumber();
            }
            dataList.realName = getLiveUserList.get(i).getRealName();
            dataList.idImageURL = getLiveUserList.get(i).getIdImageURL();
            dataList.applyLiveUserTime = getLiveUserList.get(i).getApplyTime();
            dataList.id = userDaoService.findById(getLiveUserList.get(i).getUserId()).getId();
            sendData.data.add(dataList);
        }
        sendData.code = 200;
        sendData.count = (int) getLiveUserPage.getTotal();
        return sendData;
    }
*/


    @RequestMapping("/becomeLiveUser")
    @ResponseBody
    public Object becomeLiveUser(String id, String phone, int platform, Principal principal) {
        User user = dataManager.findUser(id);
        LiveUser liveUser = liveUserBuilder.becomeLiveUserByUserAndStatue(user, Constant.LiveStatus.OFFLINE, Constant.PlatformType.forNumber(platform));
        if(Objects.isNull(liveUser)) return "is liveUser";
        liveUser.setPhoneNumber(phone);
        dataManager.saveLiveUser(liveUser);
        LiveUserApproveRecord approveRecord = liveUserApproveRecord.findRecentApproveRecordByStatue(liveUser.getId(), LiveUserApproveStatue.UNAPPROVE);
        if(Objects.isNull(approveRecord)){
            approveRecord = LiveUserApproveRecord.newInstance(liveUser.getId(), liveUser.getUserId(), liveUser.getPlatformType(), LiveUserApproveStatue.PASS);
        }
        approveRecord.approveLiveUser( LiveUserApproveStatue.PASS,principal.getName());
        liveUserApproveRecord.save(approveRecord);
        EventPublisher.publish(TaskEvent.newInstance(liveUser.getUserId(), ConditionType.becomeLiveUser, 1));
        return "success";
    }

    @RequestMapping("/getUserIdImageURLbyId")
    @ResponseBody
    public Object getUserIdImageURLbyId(HttpServletRequest request, HttpServletResponse response, String id) {
        var userIdImageUrl = liveUserDaoService.findById(id).getIdImageURL();
        var sendData = new HttpURLConnection();
        var userId = liveUserDaoService.findById(id).getUserId();
        if (userIdImageUrl == null) {
            sendData.code = 404;
            sendData.data = null;
            sendData.count = 0;
            return sendData;
        }
        sendData.code = 0;
        sendData.any = "/images/id/" + userId + "/" + userIdImageUrl + ".png";
        return sendData;
    }

    @RequestMapping("/getLiveUserList")
    @ResponseBody
    public Object getLiveUserList(HttpServletRequest request, HttpServletResponse response, int page, int size, String liveUserId, String userId, String roomId) {
        var sendData = new HttpURLConnection();
        if (StringUtils.isNullOrBlank(liveUserId) && StringUtils.isNullOrBlank(roomId) && StringUtils.isNullOrBlank(userId)) {
            var liveUserPage = liveUserDaoService.findPassLiveUser(page - 1, size);
            var liveUserList = liveUserPage.getContent();
            for (int i = 0; i < liveUserList.size(); i++) {
                var liveUser = liveUserList.get(i);
                var user = userDaoService.findById(liveUser.getUserId());
                var sendLiveUser = new AuthenticationLiveUser();
                sendLiveUser.id = liveUser.getId();
                sendLiveUser.getGameCoin = user.getGameCoin();
                sendLiveUser.phoneNumber = user.getPhoneNumber();
                sendLiveUser.displayName = user.getDisplayName();
                sendLiveUser.realName = liveUser.getRealName();
                sendLiveUser.avatarUrl = user.getAvatarUrl();
                sendLiveUser.liveStatus = liveUser.getLiveStatus();
                sendData.data.add(sendLiveUser);
            }
            sendData.code = 200;
            sendData.count = (int) liveUserPage.getTotalElements();
            return sendData;
        } else if (!StringUtils.isNullOrBlank(liveUserId)) {
            LiveUser liveUser = liveUserDaoService.findById(liveUserId);
            if (liveUser == null) {
                return "null";
            }
            if (liveUser.isScriptLiveUser()) {
                return "null";
            }
            User user = userDaoService.findById(liveUser.getUserId());
            var sendLiveUser = new AuthenticationLiveUser();
            sendLiveUser.id = liveUser.getId();
            sendLiveUser.getGameCoin = user.getGameCoin();
            sendLiveUser.phoneNumber = user.getPhoneNumber();
            sendLiveUser.displayName = user.getDisplayName();
            sendLiveUser.realName = liveUser.getRealName();
            sendLiveUser.avatarUrl = user.getAvatarUrl();
            sendLiveUser.liveStatus = liveUser.getLiveStatus();
            sendData.data.add(sendLiveUser);
            sendData.code = 200;
            sendData.count = 1;
            return sendData;
        } else if (!StringUtils.isNullOrBlank(userId)) {
            User user = userDaoService.findById(userId);
            if (user == null) {
                return "null";
            }
            if (user.getLiveUserId() == null) {
                return "null";
            }
            LiveUser liveUser = liveUserDaoService.findById(user.getLiveUserId());
            if (liveUser.getLiveStatus() != Constant.LiveStatus.OFFLINE && liveUser.getLiveStatus() != Constant.LiveStatus.ONLINE) {
                return "null";
            }
            if (liveUser.isScriptLiveUser()) {
                return "null";
            }
            var sendLiveUser = new AuthenticationLiveUser();
            sendLiveUser.id = liveUser.getId();
            sendLiveUser.getGameCoin = user.getGameCoin();
            sendLiveUser.phoneNumber = user.getPhoneNumber();
            sendLiveUser.displayName = user.getDisplayName();
            sendLiveUser.realName = liveUser.getRealName();
            sendLiveUser.avatarUrl = user.getAvatarUrl();
            sendLiveUser.liveStatus = liveUser.getLiveStatus();
            sendData.data.add(sendLiveUser);
            sendData.code = 200;
            sendData.count = 1;
            return sendData;
        } else {
            var room = roomDaoService.findByDisplayId(roomId);
            if (room == null) {
                return "null";
            }
            LiveUser liveUser = liveUserDaoService.findById(room.getLiveUserId());
            if (liveUser.getLiveStatus() != Constant.LiveStatus.OFFLINE && liveUser.getLiveStatus() != Constant.LiveStatus.ONLINE) {
                return "null";
            }
            if (liveUser.isScriptLiveUser()) {
                return "null";
            }
            User user = userDaoService.findById(liveUser.getUserId());
            var sendLiveUser = new AuthenticationLiveUser();
            sendLiveUser.id = liveUser.getId();
            sendLiveUser.getGameCoin = user.getGameCoin();
            sendLiveUser.phoneNumber = user.getPhoneNumber();
            sendLiveUser.displayName = user.getDisplayName();
            sendLiveUser.realName = liveUser.getRealName();
            sendLiveUser.avatarUrl = user.getAvatarUrl();
            sendLiveUser.liveStatus = liveUser.getLiveStatus();
            sendData.data.add(sendLiveUser);
            sendData.code = 200;
            sendData.count = 1;
            return sendData;
        }
    }

    @RequestMapping("/PassLiveUser")
    @ResponseBody
    public Object PassLiveUser(HttpServletRequest request, HttpServletResponse response, String id, Principal principal) {
        liveUserManagerPageProcess.passLiveUser(id,principal.getName());
        return "success";
    }

    @RequestMapping("/BatchPassLiveUser")
    @ResponseBody
    public Object PassLiveUser(HttpServletRequest request, HttpServletResponse response, Principal principal) {
        List<LiveUser> liveUsers = liveUserDaoService.findLiveUsersByStatus(Constant.LiveStatus.UNAPPROVED);
        List<LiveUserApproveRecord> records = new ArrayList<>(liveUsers.size());
        liveUsers.forEach(liveUser -> {
            liveUser.setLiveStatus(Constant.LiveStatus.OFFLINE);
            liveUser.setAuditTime(System.currentTimeMillis());
            var record =  LiveUserApproveRecord.newInstance(liveUser.getId(), liveUser.getUserId(), liveUser.getPlatformType(), LiveUserApproveStatue.PASS);
            record.approveLiveUser(LiveUserApproveStatue.PASS,principal.getName());
            records.add(record);
            dataManager.saveLiveUser(liveUser);
        });
        liveUserApproveRecord.saveAll(records);
        return "success";
    }

    @RequestMapping("/RejectLiveUser")
    @ResponseBody
    public Object rejectLiveUser(HttpServletRequest request, HttpServletResponse response, String id, String RejectionReasons, Principal principal) {
        var liveUserMsg = liveUserDaoService.findById(id);
        User user = dataManager.findUser(liveUserMsg.getUserId());
        user.setLiveUserId(null);
        liveUserMsg.setLiveStatus(Constant.LiveStatus.APPROVED_FAIL);
        liveUserMsg.setAuditTime(System.currentTimeMillis());
        liveUserDaoService.save(liveUserMsg);
        dataManager.saveLiveUser(liveUserMsg);
        LiveUserApproveRecord recentApproveRecord = liveUserApproveRecord.findRecentApproveRecordByStatue(id, LiveUserApproveStatue.UNAPPROVE);
        if(Objects.nonNull(recentApproveRecord)) {
            recentApproveRecord.approveLiveUser(LiveUserApproveStatue.NO_PASS, principal.getName());
            recentApproveRecord.setRejectionReasons(RejectionReasons);
            liveUserApproveRecord.save(recentApproveRecord);
        }
       /* var liveUserApprove = new LiveUserApproveRecord(principal.getName(), liveUserMsg.getId(), false, RejectionReasons, liveUserMsg.getPlatformType(), new Date());
        liveUserApproveRecord.save(liveUserApprove);*/
        dataManager.saveUser(user);
        return "success";
    }

    @RequestMapping("/getLiveUserApproveRecordList")
    @ResponseBody
    public HttpURLConnection<AuthenticationLiveUser> getLiveUserApproveRecordList(int page, int limit, String userId, boolean isPass, Long start, Long end, int platform) {
        PageInfo<AuthenticationLiveUser> passRecord = liveUserManagerPageProcess.getLiveUserApproveRecordByCondition(PageRequest.of(page - 1, limit), userId, isPass, start, end, platform);
        var sendData = new HttpURLConnection<AuthenticationLiveUser>();
        sendData.code = 0;
        sendData.count = (int) passRecord.getTotal();
        sendData.data = new ArrayList<>(passRecord.getContent());
        return sendData;
    }


    @RequestMapping("/liveUserDetailInfo")
    @ResponseBody
    public Object liveUserDetailInfo(HttpServletRequest request, HttpServletResponse response, String liveUserId) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveUserDetailInfo.html");
        if (liveUserId == null) {
            return modelAndView;
        }
        modelAndView.addObject("data", liveUserManagerPageProcess.getLiveUserDetail(liveUserId));
        return modelAndView;
    }

    @RequestMapping("/liveRecommend")
    @ResponseBody
    public Object liveRecommend(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("LiveUser/liveRecommend.html");
        return modelAndView;
    }

    @RequestMapping("/reportDetails")
    @ResponseBody
    public Object reportDetails(HttpServletRequest request, HttpServletResponse response, String id) {
        ModelAndView modelAndView = new ModelAndView("report/reportDetails.html");
        var reportDetails = reportDaoService.findById(id);
        modelAndView.addObject("id", id);
        modelAndView.addObject("reportDetails", reportDetails);
        return modelAndView;
    }

    @RequestMapping("/getAllReportList")
    @ResponseBody
    public Object getAllReportList(HttpServletRequest request, HttpServletResponse response) {
        var ReportList = reportDaoService.findAll();
        var sendData = new HttpURLConnection();
        sendData.code = 0;
        sendData.data = ReportList;
        sendData.count = ReportList.size();
        return sendData;
    }

    @RequestMapping("/fuzzyQueryReportList")
    @ResponseBody
    public Object fuzzyQueryReportList(HttpServletRequest request, HttpServletResponse response, String informantId, String reportedId, String reportedRoomId, int violationType, Date dateStart, Date dateEnd, boolean isHandle, int page, int size) throws ParseException {
        var sendData = new HttpURLConnection();
        var ReportNotDatePageObj = reportDaoService.findByFuzzyQueryReportListAndType(informantId, reportedId, reportedRoomId, Constant.ViolationType.forNumber(violationType), isHandle, dateStart, dateEnd, page - 1, size);
        var ReportNotDateList = ReportNotDatePageObj.getContent();
        sendData.code = 0;
        sendData.data = ReportNotDateList;
        sendData.count = (int) ReportNotDatePageObj.getTotalElements();
        return sendData;
    }

    @RequestMapping("/fuzzyQueryReportListNotType")
    @ResponseBody
    public Object fuzzyQueryReportListNotType(HttpServletRequest request, HttpServletResponse response, String informantId, String reportedId, String reportedRoomId, Date dateStart, Date dateEnd, boolean isHandle, int page, int size) throws ParseException {
        var sendData = new HttpURLConnection<Report>();
        var ReportNotDatePageObj = reportDaoService.findByFuzzyQueryReportList(informantId, reportedId, reportedRoomId, dateStart, dateEnd, isHandle, page - 1, size);
        var ReportNotDateList = ReportNotDatePageObj.getContent();
        sendData.code = 0;
        sendData.data = ReportNotDateList;
        sendData.count = (int) ReportNotDatePageObj.getTotalElements();
        return sendData;
    }

    @RequestMapping("/handleReportById")
    @ResponseBody
    public Object handleReportById(String id) {
        var getReport = reportDaoService.findById(id);
        getReport.setIsHandle(true);
        reportDaoService.save(getReport);
        return "success";
    }


    @RequestMapping("/insertReportData")
    @ResponseBody
    public Object insertReportData() {
        List<Report> nList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            var report = new Report();
            report.setInformantId(RandomUtil.getRandomInt(1000000, 9999999, null) + "");
            report.setInformantNickName("xidegui" + i);
            report.setContact("string");
            report.setReportedId(RandomUtil.getRandomInt(1000000, 9999999, null) + "");
            report.setReportedNickName("xidegui" + RandomUtil.getRandomInt(10, 100, null));
            report.setReportedRoomId(RandomUtil.getRandomInt(10000, 99999, null) + "");
            report.setViolationType(Constant.ViolationType.forNumber(RandomUtil.getRandomInt(0, 4, null)));
            report.setCreateDate(new Date());
            report.setContent("test");
            List<String> list = new ArrayList<>();
            list.add("/images/avatar/default/img_avatar.png");
            list.add("/images/avatar/default/img_avatar.png");
            list.add("/images/avatar/default/img_avatar.png");
            report.setImageUrlList(list);
            nList.add(report);
        }
        reportDaoService.saveAll(nList);
        return "success";
    }


    public HttpURLConnection<Object> rejectLiveUserDataInfo(PageImpl<LiveUser> getLiveUserPage) {
        var sendData = new HttpURLConnection<>();
        var getLiveUserList = getLiveUserPage.getContent();
        for (LiveUser liveUser : getLiveUserList) {
            var liveUserRecord = liveUserApproveRecord.findByApprovedUserId(liveUser.getId(), null, null);
            var dataList = new AuthenticationLiveUser();
            String userId = liveUser.getUserId();
            var user = userDaoService.findById(userId);

            if (user.getPhoneNumber() == null) {
                dataList.phoneNumber = "unbound";
            } else {
                dataList.phoneNumber = user.getPhoneNumber();
            }
            dataList.getGameCoin = user.getGameCoin();
            dataList.displayName = user.getDisplayName();
            dataList.avatarUrl = user.getAvatarUrl();
            dataList.applicationsNumber = liveUserRecord.size();
            dataList.realName = liveUser.getRealName();
            dataList.idImageURL = liveUser.getIdImageURL();
            dataList.backOfficeName = liveUserRecord.get(liveUserRecord.size() - 1).getBackOfficeName();
            dataList.id = userDaoService.findById(liveUser.getUserId()).getId();
            dataList.rejectReasons = liveUserRecord.get(liveUserRecord.size() - 1).getRejectReasons();
            dataList.applyLiveUserTime = liveUser.getApplyTime();
            sendData.data.add(dataList);
        }
        sendData.code = 200;
        sendData.count = (int) getLiveUserPage.getTotalElements();
        return sendData;
    }

    public HttpURLConnection<Object> noData() {
        var sendData = new HttpURLConnection<>();
        sendData.code = 200;
        sendData.count = 0;
        sendData.data = null;
        return sendData;
    }

    @RequestMapping("/getLiveUserSummary")
    @ResponseBody
    public Object LiveUserSummary(int platform) {
        return liveUserManagerPageProcess.getLiveUserSummary(Constant.PlatformType.forNumber(platform));
    }

    @RequestMapping("/getLiveUserPageList")
    @ResponseBody
    public HttpURLConnection<LiveUserInfo> getLiveUserList(LiveUserPageListRequest request) {
        request.setPage(request.getPage()-1);
        HttpURLConnection<LiveUserInfo> data = new HttpURLConnection<>(200, "success");
        PageInfo<LiveUserInfo> pageInfo = liveUserManagerPageProcess.queryLiveUserInfoList(request);
        data.data = new ArrayList<>(pageInfo.getContent());
        data.count = (int) pageInfo.getTotal();
        return data;
    }

    @RequestMapping("/getLiveRecordChartData")
    @ResponseBody
    public List<DailyLiveRecordInfo> getLiveRecordChartData(String liveUserId, long startTime, long endTime) {
        return liveUserManagerPageProcess.findDailyLiveRecordInfo(liveUserId, TimeUtil.getTimeDayStartTime(startTime), endTime);
    }

    @RequestMapping("/updateRoomHot")
    @ResponseBody
    public HttpURLConnection<Object> operationLiveRoomHot(boolean hot, String roomId) {
        Room onlineRoom = DataManager.findOnlineRoom(roomId);
        if (onlineRoom == null) {
            return new HttpURLConnection<>(500, "room not exit");
        }
        onlineRoom.setHot(hot);
        return new HttpURLConnection<>(200, "success");
    }

    @RequestMapping("/swapRoomRecommendSort")
    @ResponseBody
    public HttpURLConnection<Object> swapRoomRecommendSort(String swapRoomId, boolean raiseRecommend,int platform) {
        boolean result = liveUserManagerPageProcess.swapRoomRecommendSort(swapRoomId, raiseRecommend, Constant.PlatformType.forNumber(platform));
        if (!result) return new HttpURLConnection<>(200, "roomNotRecommend");
        else return new HttpURLConnection<>(200, "success");
    }

    @RequestMapping("/getRoomList")
    @ResponseBody
    public List<RoomInfo> getRoomList(String liveUserId, String displayName, String roomId, int platform) {
        try {
            logger.info("receive room list request" + liveUserId + "-" + displayName + "-" + roomId);
            return liveUserManagerPageProcess.getSortedOnlineRoomInfo(liveUserId, displayName, roomId, Constant.PlatformType.forNumber(platform));
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.getMessage());
        }
        return null;
    }

    @RequestMapping("/queryLiveRecord")
    @ResponseBody
    public PageInfo<LiveRecordInfo> queryLiveRecord(int page, int size, String liveUserId, String roomId, int platform, Long startTime,Long endTime) {
        return liveUserManagerPageProcess.queryLiveRecord(page - 1, size, liveUserId, roomId, Constant.PlatformType.forNumber(platform), startTime, endTime);
    }

    @RequestMapping("/queryLiveAttendance")
    @ResponseBody
    public PageInfo<LiveAttendance> queryLiveAttendance(boolean isStandards, long time, int page, int size) {
        return liveUserManagerPageProcess.queryLiveAttendance(isStandards, time, page - 1, size);
    }

    @RequestMapping("/changePlatform")
    @ResponseBody
    public HttpURLConnection<Object> changePlatform(String liveUserId, int plantForm) {
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(plantForm);
        if (platformType == null) return new HttpURLConnection<>(500, "plantFormIsNull");
        Constant.ResultCode resultCode = liveUserManagerPageProcess.changeLiveUserPlantForm(liveUserId, platformType);
        int code = Objects.equals(Constant.ResultCode.SUCCESS, resultCode) ? 200 : 500;
        return new HttpURLConnection<>(code, resultCode.name());
    }

    @RequestMapping("/banLiveUser")
    @ResponseBody
    public HttpURLConnection<Object> banLiveUser(String liveUserId) {
        Constant.ResultCode resultCode = liveUserManagerPageProcess.banLiveUser(liveUserId);
        int code = Objects.equals(Constant.ResultCode.SUCCESS, resultCode) ? 200 : 500;
        return new HttpURLConnection<>(code, resultCode.name());
    }

    @RequestMapping("/unLockLiveUser")
    @ResponseBody
    public HttpURLConnection<Object> unLockLiveUser(String liveUserId) {
        Constant.ResultCode resultCode = liveUserManagerPageProcess.unLockLiveUser(liveUserId);
        int code = Objects.equals(Constant.ResultCode.SUCCESS, resultCode) ? 200 : 500;
        return new HttpURLConnection<>(code, resultCode.name());
    }

    @RequestMapping("/updateBanLivePermission")
    @ResponseBody
    public HttpURLConnection<Object> updateBanLivePermission(String liveUserId, String permissions) {
        List<String> permissionsList = new Gson().fromJson(permissions, new TypeToken<List<String>>() {}.getType());
        permissionsList = Optional.ofNullable(permissionsList).orElse(new ArrayList<>());
        Constant.ResultCode resultCode = liveUserManagerPageProcess.updateLiveUserBanPermission(liveUserId,permissionsList);
        int code = Objects.equals(Constant.ResultCode.SUCCESS, resultCode) ? 200 : 500;
        return new HttpURLConnection<>(code, resultCode.name().toLowerCase());
    }
    @RequestMapping("/getLiveSourceLineList")
    @ResponseBody
    public HttpURLConnection<Constant.LiveSourceLine> getLiveSourceLineList() {
        LiveSourceLineConfig liveSourceLineConfig = liveSourceLineConfigDaoService.findLiveSourceLineConfigByPlatformType(Constant.PlatformType.PLATFORM_JINLI);
        HttpURLConnection<Constant.LiveSourceLine> result = new HttpURLConnection<>(200, "success");
        result.data = new ArrayList<>(liveSourceLineConfig.getAvailableLine());
        return result;
    }

    @RequestMapping("/openOrCloseLiveSourceLine")
    @ResponseBody
    public HttpURLConnection<Object> openOrCloseLiveSourceLine(Constant.LiveSourceLine liveSourceLine, boolean close) {
        liveUserManagerPageProcess.openOrCloseLiveSourceLine(liveSourceLine, close);
        return new HttpURLConnection<>(200, "success");
    }

    @RequestMapping("/queryBanLiveUser")
    @ResponseBody
    public PageInfo<LiveUserInfo> queryBanLiveUser(int platform, String liveUserId, String liveUserName, String roomId, int page, int size, Long startTime , Long endTime){
        return liveUserManagerPageProcess.queryBanLiveUserInfoList(platform, liveUserId, liveUserName, roomId, page, size, startTime, endTime);
    }

    @RequestMapping("/unLockLiveList")
    @ResponseBody
    public HttpURLConnection<Object> unLockLiveList(@RequestBody List<String> ids) {
        Constant.ResultCode resultCode = liveUserManagerPageProcess.unLockLiveList(ids);
        int code = Objects.equals(Constant.ResultCode.SUCCESS, resultCode) ? 200 : 500;
        return new HttpURLConnection<>(code, resultCode.name());
    }

    /*平台管理 直播记录*/
    @RequestMapping("/queryPlatformLiveRecordInfo")
    @ResponseBody
    public PageInfo<LiveRecordInfo> queryPlatformLiveRecordInfo(LiveRecordRequest recordRequest){
        if(Objects.nonNull(recordRequest.getEndTime())){
            recordRequest.setEndTime(TimeUtil.getDayEndTime(recordRequest.getEndTime()));
        }
        return liveUserManagerPageProcess.queryPlatformLiveRecordByLiveRecordRequest(recordRequest);
    }

    //=========LiveLimit API===========
    /*上传直播限制表*/
    @PostMapping("/uploadLiveLimitExcel")
    @ResponseBody
    public HttpURLConnection<String> uploadLiveUserLiveExcel(@RequestParam("file") MultipartFile multipartFile ,int platform){
        ImportParams params = new ImportParams();
        params.setHeadRows(1);
        params.setNeedVerify(true);
        Map<Long, List<LiveTimeImportRequest>> limitMap = MyEasyPoiUtil.importMultipleSheetExcel(multipartFile, 0, 2, LiveTimeImportRequest.class);
        if(limitMap.isEmpty()) return new HttpURLConnection<>(GameConstant.UPLOAD_FILE_ERROR, "file not standard");
        int resultCode = liveLimitProcess.uploadLiveLimitList(limitMap, Constant.PlatformType.forNumber(platform));
        String message = "success";
        if(resultCode != GameConstant.SUCCESS) {
            message = "live user not exit or not belong to platform";
        }
        return new HttpURLConnection<>(resultCode, message);
    }

    /*添加直播限制*/
    @PostMapping("/addLiveLimitList")
    @ResponseBody
    public HttpURLConnection<String> addLiveLimitList(AddLiveLimitListRequest request, Principal principal){
        int resultCode = liveLimitProcess.uploadLiveLimitListByAddLiveLimitListRequest(request,principal.getName());
        String message = "success";
        if(resultCode == GameConstant.PARAM_ERROR) {
            message = "param error";
        }else if(resultCode == GameConstant.LIVE_USER_NOT_EXIT) {
            message = "live user not exit or not belong to platform";
        }
        return new HttpURLConnection<>(resultCode, message);
    }
    /*查询直播限制记录*/
    @RequestMapping("/requestLiveLimitAddRecord")
    @ResponseBody
    public PageInfo<LiveLimitRecord> requestLiveLimitAddRecord(LiveLimitRecordRequest request) {
       return liveLimitProcess.findLiveLimitAddRecord(request);
    }

    /*查询直播限制表*/
    @RequestMapping("/requestLiveLimitList")
    @ResponseBody
    public LiveLimitListReply requestLiveLimitList(LiveLimitListRequest request) {
        return liveLimitProcess.requestLiveLimitList(request);
    }

    @RequestMapping("/updateLiveUserSharedPlatform")
    @ResponseBody
    public GenericResponse updateLiveUserSharedPlatform(LiveUserPlatformRequest request){
        return liveUserManagerPageProcess.updateLiveUserSharedPlatform(request);
    }
}

