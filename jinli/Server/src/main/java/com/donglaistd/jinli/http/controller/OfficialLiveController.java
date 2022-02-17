package com.donglaistd.jinli.http.controller;


import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.builder.LiveUserBuilder;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.backoffice.OfficialLiveRecord;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.http.entity.OfficialLiveSend;
import com.donglaistd.jinli.http.entity.OfficialLiveUserReturn;
import com.donglaistd.jinli.http.service.OfficialLiveService;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("backOffice/")
public class OfficialLiveController {
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    GameBuilder gameBuilder;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    OfficialLiveRecordDaoService officialLiveRecordDaoService;
    @Autowired
    OfficialLiveService officialLiveService;
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;

    @RequestMapping("/backOfficeUser/officialLiveList")
    @ResponseBody
    public ModelAndView officialLiveList(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("OfficialLive/officialLiveList.html");
        return modelAndView;
    }

    @RequestMapping("/backOfficeUser/newOfficialLive")
    @ResponseBody
    public ModelAndView newOfficialLive(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("OfficialLive/newOfficialLive.html");
        return modelAndView;
    }

    @RequestMapping("/backOfficeUser/officialLiveRecords")
    @ResponseBody
    public ModelAndView officialLiveRecords(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("OfficialLive/officialLiveRecords.html");
        return modelAndView;
    }

    @RequestMapping("/officialLive/addOfficialLive")
    @ResponseBody
    public Object addOfficialLive(HttpServletRequest request,HttpServletResponse response,
                                  String roomName,String liveUserName,String liveUserImg,
                                  String roomDisplayId,String roomImg, int gameId,boolean banker){
        if(officialLiveService.liveUserRepeat(liveUserName,roomDisplayId)){
            return "liveUser";
        }
        OfficialLiveUserReturn liveUserPair = officialLiveService.createLiveUser(liveUserName,liveUserImg,roomName,roomImg,roomDisplayId);
        if(liveUserPair.resultCode == Constant.ResultCode.ROOM_ID_REPEATED){
            return "roomDisplayId";
        }
        else if(liveUserPair.resultCode == Constant.ResultCode.ROOM_TITLE_REPEATED){
            return "roomName";
        }
        LiveUser liveUser = liveUserPair.liveUser;
        Constant.GameType type = Constant.GameType.forNumber(gameId);
        Room room = liveUserPair.room;
        officialLiveService.createGame(liveUser,type,banker);
        dataManager.saveLiveUser(liveUser);
        var officialLiveRecord = new OfficialLiveRecord();
        officialLiveRecord.setLiveUserId(liveUser.getId());
        officialLiveRecord.setRoomCreateDate(System.currentTimeMillis());
        officialLiveRecord.setGameType(type);
        officialLiveRecord.setOpen(true);
        officialLiveRecord.setRoomId(room.getId());
        officialLiveRecord.setLiveUserId(liveUser.getId());
        officialLiveRecord.setRoomDisplayId(room.getDisplayId());
        officialLiveRecord.setRoomName(room.getRoomTitle());
        officialLiveRecordDaoService.save(officialLiveRecord);
        return "success";
    }

    @RequestMapping("/officialLive/getOfficialLiveList")
    @ResponseBody
    public HttpURLConnection<OfficialLiveSend> getOfficialLiveList(HttpServletRequest request,HttpServletResponse response,int page ,int size){
        var OfficialLiveRecordPage =  officialLiveRecordDaoService.findByIsClose(true,page-1,size);
        List<OfficialLiveRecord> OfficialLiveRecord = OfficialLiveRecordPage.getContent();
        List<OfficialLiveRecord> OfficialLiveRecordList = officialLiveService.getOpenOfficialLiveInfos(OfficialLiveRecord);
        ArrayList<OfficialLiveSend> sendData = new ArrayList<>();
        for (OfficialLiveRecord data : OfficialLiveRecordList) {
            OfficialLiveSend sendObj = new OfficialLiveSend();
            sendObj.setRoomName(data.getRoomName());
            sendObj.setRoomDisplayId(data.getRoomDisplayId());
            sendObj.setRoomId(data.getRoomId());
            sendObj.setGameType(data.getGameType());
            sendObj.setGiftTurnover(data.getGiftCoinAmount());
            sendObj.setGameTurnover(data.getGamePlayCoinAmount());
            sendObj.setRoomPeople((int) data.getHistoryCount());
            sendObj.setNowRoomPeople((int) data.getCurrentCount());
            sendObj.setRoomCreateDate(data.getRoomCreateDate());
            sendObj.setLiveUserId(data.getLiveUserId());
            sendData.add(sendObj);
        }
        var send = new HttpURLConnection<OfficialLiveSend>();
        send.count = (int)OfficialLiveRecordPage.getTotalElements();
        send.code = 200;
        send.data = sendData;
        return send;
    }

    @RequestMapping("/officialLive/closeOfficialLive")
    @ResponseBody
    public Object closeOfficialLive(HttpServletRequest request, HttpServletResponse response, String liveUserId, Principal principal){
        return officialLiveService.closeOfficialLive(liveUserId,principal.getName());
    }

    @RequestMapping("/officialLive/getCloseOfficialList")
    @ResponseBody
    public Object getCloseOfficialList(HttpServletRequest request,HttpServletResponse response,int page,int size,Long startTime , Long endTime, int gameType){
        PageImpl<OfficialLiveRecord> officialLiveRecordPage = officialLiveRecordDaoService.findCloseRoom(page-1,size,startTime,endTime,Constant.GameType.forNumber(gameType));
        var send = new HttpURLConnection();
        send.count = (int)officialLiveRecordPage.getTotalElements();
        send.code = 200;
        send.data = closeOfficialListDataInfo(officialLiveRecordPage.getContent());
        return send;

    }

    public Object noData(){
        var sendData = new HttpURLConnection();
        sendData.code = 200;
        sendData.count = 0;
        sendData.data = new ArrayList();
        return sendData;
    }

    public ArrayList<OfficialLiveSend> closeOfficialListDataInfo(List<OfficialLiveRecord> OfficialLiveRecordList){
        ArrayList<OfficialLiveSend> sendData = new ArrayList<>();
        for(int i=0;i<OfficialLiveRecordList.size();i++){
            OfficialLiveRecord data = OfficialLiveRecordList.get(i);
            OfficialLiveSend sendObj = new OfficialLiveSend();
            sendObj.setRoomName(data.getRoomName());
            sendObj.setRoomDisplayId(data.getRoomDisplayId());
            sendObj.setRoomId(data.getRoomId());
            sendObj.setGameType(data.getGameType());
            sendObj.setGiftTurnover(data.getGiftCoinAmount());
            sendObj.setGameTurnover(data.getGamePlayCoinAmount());
            sendObj.setRoomPeople((int)data.getHistoryCount());
            sendObj.setRoomCloseDate(data.getRoomCloseDate());
            sendObj.setBackOfficeName(data.getBackOfficeName());
            sendData.add(sendObj);
        }
        return sendData;
    }
}
