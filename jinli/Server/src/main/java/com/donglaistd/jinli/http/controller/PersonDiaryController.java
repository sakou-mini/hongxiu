package com.donglaistd.jinli.http.controller;


import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.backoffice.PersonDiaryOperationlog;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.http.dto.request.DiaryListRequest;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.PersonDiaryListSend;
import com.donglaistd.jinli.http.entity.RecommendDiaryDetail;
import com.donglaistd.jinli.http.response.GenericResponse;
import com.donglaistd.jinli.http.response.SuccessResponse;
import com.donglaistd.jinli.service.DiaryProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("backOffice/")
public class PersonDiaryController {

    private static final Logger logger = Logger.getLogger(BackOfficeController.class.getName());

    @Autowired
    private PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private BackOfficeUserDaoService backOfficeUserDaoService;
    @Autowired
    private PersonDiaryOperationLogDaoService logDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    ZoneDaoService zoneDaoService;
    @Autowired
    DiaryResourceDaoService diaryResourceDaoService;
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    DiaryProcess diaryProcess;

    @RequestMapping("/liveUser/personDiaryList")
    @ResponseBody
    public Object personDiaryList(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("PersonDiary/personDiaryList.html");
        modelAndView.addObject("message", "success");
        return modelAndView;
    }

    @RequestMapping("/liveUser/personDiaryDetails")
    @ResponseBody
    public Object personDiaryDetails(HttpServletRequest request, HttpServletResponse response, String id) {
        ModelAndView modelAndView = new ModelAndView("PersonDiary/personDiaryDetails.html");
        var sendMsg= personDiaryDaoService.findById(id);
        if(sendMsg == null){
            return new ModelAndView("PersonDiary/personDiaryReject.html");
        }
        var sendData = new PersonDiaryListSend();
        sendData.userId = sendMsg.getUserId();
        sendData.uploadTime = sendMsg.getUploadTime();
        sendData.type = sendMsg.getType();
        sendData.state = sendMsg.getStatue();
        sendData.displayName = userDaoService.findById(sendMsg.getUserId()).getDisplayName();
        sendData.id = sendMsg.getId();
        sendData.resourceUrlLis = diaryResourceDaoService.findDiaryResource(sendData.id);
        sendData.content = sendMsg.getContent();
        if(sendMsg.getStatue() == Constant.DiaryStatue.forNumber(3)){
            sendData.rejectContent = logDaoService.findByPersonDiaryIdAndIsApproval(id,false).getRejectContent();
        }
        modelAndView.addObject("data", sendData);
        return modelAndView;
    }

    @RequestMapping("/liveUser/personDiaryApproved")
    @ResponseBody
    public Object personDiaryApproved(HttpServletRequest request, HttpServletResponse response, String id) {
        ModelAndView modelAndView = new ModelAndView("PersonDiary/personDiaryApproved.html");
        return modelAndView;
    }

    @RequestMapping("/liveUser/personDiaryReject")
    @ResponseBody
    public Object personDiaryReject(HttpServletRequest request, HttpServletResponse response, String id) {
        ModelAndView modelAndView = new ModelAndView("PersonDiary/personDiaryReject.html");
        return modelAndView;
    }

    @RequestMapping("/personDiary/getPersonDiaryByUserIdAndTime")
    @ResponseBody
    public Object getPersonDiaryByUserIdAndTime(HttpServletRequest request, HttpServletResponse response, String id,Long startTime,Long endTime,int page ,int size) {
        var getPersonDiaryAndPage = personDiaryDaoService.findAllUnapprovedByTimeRangeAndUserId(id,startTime,endTime,Constant.DiaryStatue.forNumber(1),page-1,size);
        var getPersonDiaryAllList = getPersonDiaryAndPage.getContent();
        var sendData = new HttpURLConnection<>();
        var sendArr = new ArrayList<>();
        for (PersonDiary personDiary : getPersonDiaryAllList) {
            var sendObj = new PersonDiaryListSend();
            var userMsg = userDaoService.findById(personDiary.getUserId());
            sendObj.content = personDiary.getContent();
            sendObj.displayName = userMsg.getDisplayName();
            sendObj.id = personDiary.getId();
            sendObj.resourceUrlLis = diaryResourceDaoService.findDiaryResource(sendObj.id);
            sendObj.state = personDiary.getStatue();
            sendObj.type = personDiary.getType();
            sendObj.uploadTime = personDiary.getUploadTime();
            sendObj.userId = personDiary.getUserId();
            sendArr.add(sendObj);
        }
        sendData.data = sendArr;
        sendData.count = (int)getPersonDiaryAndPage.getTotalElements();
        sendData.code = 0;
        return sendData;
    }

    @RequestMapping("/personDiary/OperationPersonDiary")
    @ResponseBody
    public Object OperationPersonDiary(HttpServletRequest request, HttpServletResponse response, String id , boolean isApproval, String rejectContent, Principal principal) {
        var personDiaryMsg = personDiaryDaoService.findById(id);
        var backOfficeUserMsg = backOfficeUserRepository.findByAccountName(principal.getName());
        var backOfficeLog = Optional.ofNullable(logDaoService.findByPersonDiaryId(id)).orElse(new PersonDiaryOperationlog());
        if(isApproval){
            personDiaryMsg.setStatue(Constant.DiaryStatue.forNumber(2));
            personDiaryDaoService.save(personDiaryMsg);
        }else {
            personDiaryMsg.setStatue(Constant.DiaryStatue.forNumber(3));
            personDiaryDaoService.save(personDiaryMsg);
            backOfficeLog.setRejectContent(rejectContent);
            diaryProcess.cancelRecommendDiary(id);
        }
        backOfficeLog.setOperationDate(new Date());
        backOfficeLog.setApproval(isApproval);
        backOfficeLog.setBackOfficeId(backOfficeUserMsg.getId());
        backOfficeLog.setPersonDiaryId(id);
        logDaoService.save(backOfficeLog);
      return null;
    }

    @RequestMapping("/personDiary/passAll")
    @ResponseBody
    public String passAll(Principal principal){
        List<PersonDiary> diaries = personDiaryDaoService.findDiaryByStatue(Constant.DiaryStatue.DIARY_UNAPPROVED);
        for (PersonDiary diary : diaries) {
            diary.setStatue(Constant.DiaryStatue.DIARY_APPROVAL_PASS);
        }
        personDiaryDaoService.saveAll(diaries);
        return "SUCCESS";
    }

    @RequestMapping("/personDiary/getPersonDiaryApprovedList")
    @ResponseBody
    public HttpURLConnection<PersonDiaryListSend> getPersonDiaryApprovedList(String id, Long startTime,Long endTime,int recommend,int page,int size) {
        if(endTime!=null) endTime = TimeUtil.getDayEndTime(endTime);
        PageInfo<PersonDiaryListSend> pageResult = diaryProcess.getApprovedDiaryList(id, startTime, endTime, recommend, PageRequest.of(page - 1, size));
        HttpURLConnection<PersonDiaryListSend> sendData = new HttpURLConnection<>();
        sendData.data = new ArrayList<>(pageResult.getContent());
        sendData.count = (int)pageResult.getTotal();
        sendData.code = 200;
        return sendData;
    }

    @RequestMapping("/personDiary/getPersonDiaryRejectList")
    @ResponseBody
    public Object getPersonDiaryRejectList(HttpServletRequest request, HttpServletResponse response,  String id,Long startTime,Long endTime,int page,int size) {
        var getPersonDiaryAndPage = personDiaryDaoService.findAllUnapprovedByTimeRangeAndUserId(id,startTime,endTime,Constant.DiaryStatue.forNumber(3),page-1,size);
        var getPersonDiaryAllList = getPersonDiaryAndPage.getContent();
        var sendData = new HttpURLConnection();
        var sendArr = new ArrayList<>();
        for (PersonDiary personDiary : getPersonDiaryAllList) {
            var sendObj = new PersonDiaryListSend();
            var userMsg = userDaoService.findById(personDiary.getUserId());
            sendObj.content = personDiary.getContent();
            sendObj.displayName = userMsg.getDisplayName();
            sendObj.id = personDiary.getId();
            sendObj.resourceUrlLis = diaryResourceDaoService.findDiaryResource(sendObj.id);
            sendObj.state = personDiary.getStatue();
            sendObj.type = personDiary.getType();
            sendObj.uploadTime = personDiary.getUploadTime();
            sendObj.userId = personDiary.getUserId();
            var logMsg = logDaoService.findByPersonDiaryIdAndIsApproval(personDiary.getId().toString(), false);
            var backOfficeMsg = backOfficeUserDaoService.findById(logMsg.getBackOfficeId());
            sendObj.operationDate = logMsg.getOperationDate();
            sendObj.backOfficeName = backOfficeMsg.getAccountName();
            sendArr.add(sendObj);
        }
        sendData.data = sendArr;
        sendData.count = (int)getPersonDiaryAndPage.getTotalElements();
        sendData.code = 0;
        return sendData;
    }

    @RequestMapping("/personDiary/recommendDiaryDetail")
    @ResponseBody
    public RecommendDiaryDetail recommendDiaryDetail(String id){
        return diaryProcess.getRecommendDiaryDetail(id);
    }

    @RequestMapping("/personDiary/recommend")
    @ResponseBody
    public HttpURLConnection<String> recommendDiary(String id, Constant.DiaryRecommendTimeType recommendTime, int position){
        Constant.ResultCode resultCode = diaryProcess.recommendDiary(id, recommendTime, position);
        if(!Objects.equals(Constant.ResultCode.SUCCESS,resultCode)){
            return new HttpURLConnection<>(500, resultCode.toString().toLowerCase());
        }
        return new HttpURLConnection<>(200, resultCode.toString().toLowerCase());
    }

    @RequestMapping("/personDiary/cancelRecommend")
    @ResponseBody
    public  HttpURLConnection<String> cancelRecommendDiary(String id){
        diaryProcess.cancelRecommendDiary(id);
        return new HttpURLConnection<>(200,"success");
    }

    @RequestMapping("/personDiary/diaryList")
    @ResponseBody
    public GenericResponse diaryList(DiaryListRequest request){
        request.setPage(request.getPage()-1);
        PageInfo<PersonDiaryListSend> diaryList = diaryProcess.findDiaryList(request);
        return new SuccessResponse().withData(diaryList);
    }
}

