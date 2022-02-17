package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.BackOfficeUserRepository;
import com.donglaistd.jinli.database.dao.ReportDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.accusation.Report;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.processors.handler.zone.CreatePersonDiaryRequestHandler;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportControllerTest  extends BaseTest {
    @Autowired
    LiveUserController reportController;
    @Autowired
    CreatePersonDiaryRequestHandler createPersonDiaryRequestHandler;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    ReportDaoService reportDaoService;
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private MockMvc mockMvc;

    @Before
    @Override
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
    }

    @Test
    public void getAllReportListTest() throws Exception{
        String url = "/backOffice/liveUser/getAllReportList";
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
        Gson gson = new Gson();
        String resData = mockMvc.perform(httpRequest).andReturn().getResponse().getContentAsString();
        HttpURLConnection data =gson.fromJson(resData,HttpURLConnection.class);
        System.out.println("get all data size:"+data.data.size());
    }

    @Test
    public void fuzzyQueryReportListTest() throws  Exception{
        String informantId = "";
        String reportedId = "";
        String reportedRoomId = "";
        int violationType = 0;
        String page = "1";
        String size = "10";
        String isHandle = "false";
        String url = "/backOffice/liveUser/fuzzyQueryReportList?informantId="+informantId+"&reportedId="+reportedId+"&reportedRoomId="+reportedRoomId+"&violationType="+violationType+"&isHandle="+isHandle+"&page="+page+"&size="+size;
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
        Gson gson = new Gson();
        String resData = mockMvc.perform(httpRequest).andReturn().getResponse().getContentAsString();
        HttpURLConnection data =gson.fromJson(resData,HttpURLConnection.class);
        System.out.println("get data size:"+data.count);
        for(int i = 0; i <data.data.size();i++){
            System.out.println(data.data.get(i));
        }
    }

    @Test
    public void handleReportByIdTest() throws Exception{
        Report testReport = new Report();
        testReport.setIsHandle(false);
        testReport.setCreateDate(new Date(1602663735));
        testReport.setViolationType(Constant.ViolationType.forNumber(2));
        testReport.setInformantId("123456789");
        testReport.setReportedId("8556464646");
        testReport.setInformantNickName("xxxa");
        testReport.setContact("cscscscscs");
        String id = testReport.getId();
        reportDaoService.save(testReport);
        String url = "/backOffice/liveUser/handleReportById?id="+id;
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
        Report getTestReport = reportDaoService.findById(id);
        System.out.println("is handle:"+getTestReport.getIsHandle());
    }
}
