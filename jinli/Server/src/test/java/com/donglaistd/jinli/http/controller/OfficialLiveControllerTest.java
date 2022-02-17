package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.OfficialLiveRecordDaoService;
import com.donglaistd.jinli.database.entity.backoffice.OfficialLiveRecord;
import com.donglaistd.jinli.http.service.OfficialLiveService;
import com.donglaistd.jinli.service.EventPublisher;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OfficialLiveControllerTest extends BaseTest {
    @Autowired
    OfficialLiveController officialLiveController;

    private MockMvc mockMvc;
    @Autowired
    OfficialLiveService officialLiveService;
    @Autowired
    OfficialLiveRecordDaoService officialLiveRecordDaoService;

    @Before
    @Override
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(officialLiveController).build();
    }

    public String newOfficialLive(String roomName, String liveUserName,int patterns, Constant.GameType gameType, boolean banker) {
        String url = "/backOffice/officialLive/addOfficialLive";
        RequestBuilder httpRequest = MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).param("roomName",roomName).
        param("liveUserName",liveUserName).
        param("liveUserImg","/liveUserImage").
        param("roomImg","/roomImage").
        param("patterns",String.valueOf(patterns)).
        param("gameId",String.valueOf(gameType.getNumber())).
        param("banker",""+banker);
        try {
            ResultActions resultActions = mockMvc.perform(httpRequest).andExpect(status().isOk());
            Gson gson = new Gson();
            String resData = resultActions.andReturn().getResponse().getContentAsString();
            return resData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Test
    public void newOfficialLiveTest(){
        String result = newOfficialLive("room133", "liveUser1333", 1, Constant.GameType.GOLDENFLOWER, true);
        Assert.assertEquals("success",result);
    }

    @Test
    public void queryOpenOfficialLiveTest(){
        String result =  newOfficialLive("room133", "liveUser1333", 1, Constant.GameType.GOLDENFLOWER, true);
        Assert.assertEquals("success",result);
        PageImpl<OfficialLiveRecord> openRoomRecords = officialLiveRecordDaoService.findByIsClose(true, 0, 20);
        Assert.assertEquals(1, openRoomRecords.getContent().size());
        List<OfficialLiveRecord> openOfficialLiveInfos = officialLiveService.getOpenOfficialLiveInfos(openRoomRecords.getContent());
        Assert.assertEquals(1, openOfficialLiveInfos.size());

        PageImpl<OfficialLiveRecord> closeRoomRecords  = officialLiveRecordDaoService.findByIsClose(false, 0, 20);
        Assert.assertEquals(0, closeRoomRecords.getContent().size());
        openOfficialLiveInfos = officialLiveService.getOpenOfficialLiveInfos(closeRoomRecords.getContent());
        Assert.assertEquals(0, openOfficialLiveInfos.size());
    }

    public boolean closeOfficialRoom(String liveUserId){
        String url = "/backOffice/officialLive/closeOfficialLive";
        var principal = Mockito.mock(Principal.class);
        RequestBuilder httpRequest = MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).param("liveUserId", liveUserId)
                .principal(principal);
        try {
            ResultActions resultActions = mockMvc.perform(httpRequest).andExpect(status().isOk());
            Gson gson = new Gson();
            boolean resData = Boolean.parseBoolean(resultActions.andReturn().getResponse().getContentAsString());
            return resData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Test
    public void closeOfficialRoomTest(){
        EventPublisher.isEnabled = new AtomicBoolean(true);
        String result = newOfficialLive("officialLiveRoom1", "officialLiveUser1", 1, Constant.GameType.JIAOYOU, true);
        Assert.assertEquals("success",result);
        PageImpl<OfficialLiveRecord> openRoomRecords = officialLiveRecordDaoService.findByIsClose(true, 0, 20);
        Assert.assertEquals(1, openRoomRecords.getTotalElements());
        OfficialLiveRecord liveRecord = openRoomRecords.getContent().get(0);
        String liveUserId = liveRecord.getLiveUserId();
        boolean closeResult = closeOfficialRoom(liveUserId);
        Assert.assertTrue(closeResult);
        try {
            Thread.sleep(10500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PageImpl<OfficialLiveRecord> closedLive = officialLiveRecordDaoService.findByIsClose(false, 0, 20);
        Assert.assertEquals(1, closedLive.getTotalElements());
    }

}
