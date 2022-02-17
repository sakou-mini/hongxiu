package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.BackOfficeUserRepository;
import com.donglaistd.jinli.database.dao.DiaryResourceDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.dao.ZoneDaoService;
import com.donglaistd.jinli.database.entity.zone.DiaryResource;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.database.entity.zone.Zone;
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
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonDiaryControllerTest extends BaseTest {
    @Autowired
    PersonDiaryController personDiaryController;
    @Autowired
    CreatePersonDiaryRequestHandler createPersonDiaryRequestHandler;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    ZoneDaoService zoneDaoService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    DiaryResourceDaoService diaryResourceDaoService;

    private MockMvc mockMvc;

    @Before
    @Override
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(personDiaryController).build();
    }


    @Test
    public void personDiaryApprovedTest()throws Exception{
        createImageDiary(user.getId(),"test data",1,Constant.DiaryStatue.DIARY_UNAPPROVED);
        String id = user.getId();
        String startTime = "1602644923434";
        String endTime = "1602844923647";
        String page = "1";
        String size = "10";
        String url = "/backOffice/personDiary/getPersonDiaryByUserIdAndTime?id="+id+"&startTime="+startTime+"&endTime="+endTime+"&page="+page+"&size="+size;
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
        Gson gson = new Gson();
        String resData = mockMvc.perform(httpRequest).andReturn().getResponse().getContentAsString();
        HttpURLConnection data =gson.fromJson(resData,HttpURLConnection.class);
        System.out.println("get all data size:"+data.data.size());
    }

    public List<PersonDiary> createImageDiary(String userId, String text, int num ,Constant.DiaryStatue statue){
        List<PersonDiary> diaryList = new ArrayList<>();
        for (int i = 0; i <num ; i++) {
            PersonDiary personDiary = PersonDiary.newInstance(userId, text+":image:"+num, Constant.DiaryType.IMAGE,2);
            personDiaryDaoService.save(personDiary);
            List<DiaryResource> resourceList = new ArrayList<>();
            resourceList.add(DiaryResource.newInstance(personDiary.getId(),""+1));
            resourceList.add(DiaryResource.newInstance(personDiary.getId(),""+2));
            resourceList.add(DiaryResource.newInstance(personDiary.getId(),""+3));
            resourceList.add(DiaryResource.newInstance(personDiary.getId(),""+4));
            diaryResourceDaoService.saveAllResource(resourceList);
            personDiary.setStatue(statue);
            personDiaryDaoService.save(personDiary);
            diaryList.add(personDiary);
        }
        return diaryList;
    }

    @Test
    public void getPersonDiaryApprovedListTest() throws Exception{
        createImageDiary(user.getId(),"test data",1, Constant.DiaryStatue.DIARY_APPROVAL_PASS);
        String id = user.getId();
        String startTime = "1602644923434";
        String endTime = "1602844923647";
        String page = "1";
        String size = "10";
        String url = "/backOffice/personDiary/getPersonDiaryApprovedList?id="+id+"&startTime="+startTime+"&endTime="+endTime+"&page="+page+"&size="+size+"&recommend=0";
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
        Gson gson = new Gson();
        String resData = mockMvc.perform(httpRequest).andReturn().getResponse().getContentAsString();
        HttpURLConnection data =gson.fromJson(resData,HttpURLConnection.class);
        System.out.println(data.data);
    }
}
