package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.http.controller.LiveUserController;
import com.donglaistd.jinli.http.controller.RtmpAuthController;
import com.donglaistd.jinli.http.controller.UploadController;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.processors.handler.BecomeLiveUserRequestHandler;
import com.donglaistd.jinli.processors.handler.LoginRequestHandler;
import com.donglaistd.jinli.processors.handler.zone.CreatePersonDiaryRequestHandler;
import com.donglaistd.jinli.util.DataManager;
import com.google.gson.Gson;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.InvocationTargetException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LiveUserControllerTest extends BaseTest {
    @Autowired
    LiveUserController liveUserController;
    @Autowired
    CreatePersonDiaryRequestHandler createPersonDiaryRequestHandler;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
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
        mockMvc = MockMvcBuilders.standaloneSetup(liveUserController).build();
    }

    @Test
    public void getAppLiveUserListTest() throws Exception{
        String page = "1";
        String size = "10";
        String url = "/backOffice/liveUser/getAppLiveUserList?page="+page+"&limit="+size+"&type=0&platform="+ Constant.PlatformType.PLATFORM_JINLI_VALUE;
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
        Gson gson = new Gson();
        String resData = mockMvc.perform(httpRequest).andReturn().getResponse().getContentAsString();
        HttpURLConnection data =gson.fromJson(resData,HttpURLConnection.class);
        System.out.println("should size："+size+" ,get size："+data.data.size()+" ,get count："+data.count);
    }

    @Test
    public void getUserIdImageURLbyIdTest() throws Exception{
        liveUser.setIdImageURL("13220332");
        liveUser.setUserId("sd645s6d54s65d445s54d");
        liveUserDaoService.save(liveUser);
        String id = liveUser.getId();
        String url = "/backOffice/liveUser/getUserIdImageURLbyId?id="+id;
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        Gson gson = new Gson();
        String resData = mockMvc.perform(httpRequest).andReturn().getResponse().getContentAsString();
        HttpURLConnection data =gson.fromJson(resData,HttpURLConnection.class);
        System.out.println("imgUrl:"+data.any);
    }

    @Test
    public void getPassLiveUserListTest() throws Exception{
        String page = "1";
        String size = "10";
        String url = "/backOffice/liveUser/getLiveUserPageList?page="+page+"&size="+size+"&platform="+ Constant.PlatformType.PLATFORM_JINLI_VALUE;
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
        Gson gson = new Gson();
        String resData = mockMvc.perform(httpRequest).andReturn().getResponse().getContentAsString();
        HttpURLConnection data =gson.fromJson(resData,HttpURLConnection.class);
        System.out.println("query size:"+size+" ,get size:"+data.data.size()+" ,get count:"+data.count);
    }
}
