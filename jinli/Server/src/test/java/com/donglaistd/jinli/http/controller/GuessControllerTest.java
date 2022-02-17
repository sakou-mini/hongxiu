package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.BackOfficeUserRepository;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.dao.ZoneDaoService;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.donglaistd.jinli.http.entity.GuessItem;
import com.donglaistd.jinli.processors.handler.zone.CreatePersonDiaryRequestHandler;
import com.google.gson.Gson;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class GuessControllerTest extends BaseTest {
    private final static Logger logger = LoggerFactory.getLogger(GuessController.class.getName());
    @Autowired
    GuessController guessController;
    @Autowired
    CreatePersonDiaryRequestHandler createPersonDiaryRequestHandler;
    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    ZoneDaoService zoneDaoService;
    @Autowired
    PasswordEncoder passwordEncoder;
    private MockMvc mockMvc;

    @Before
    @Override
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(guessController).build();
    }
    @Test
    public void getGuessByTimeTest() throws  Exception{
        insertTestGuessData();
        String url = "/backOffice/guess/lottery/getWaitPrizeList?page=1&size=10";
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
    }

    @Test
    public void getWagerOverGuess() throws  Exception{
        insertTestGuessData();
        String url ="/backOffice/guess/getWagerOverGuess?page=1&size=10&guessType=0";
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
    }

    @Test
    public void getGuessStatisticsListTest () throws  Exception{
        String url = "/backOffice/guess/lottery/getGuessStatisticsList?page=1&size=10&guessType=0";
        RequestBuilder httpRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(httpRequest).andExpect(status().isOk());
    }

    @Test
    public void addNewGuessTest(){
        Long nowTime = System.currentTimeMillis();
        var guessImg = "/testImg";
        String guessType = "1";
        List<String> itemList = new ArrayList();
        itemList.add("test1");
        itemList.add("test2");
        itemList.add("test3");
        Long showEndTime = nowTime+100;
        Long showStartTime=nowTime+10000;
        Long wagerStartTime=nowTime+200;
        Long wagerEndTime = nowTime+1000;
        String subtitle = "testSubtitle";
        String title = "testTitle";
        String windowImg = "/testImg";
        String sort = "1";
        Gson gson2=new Gson();
        String str=gson2.toJson(itemList);
        RequestBuilder req = MockMvcRequestBuilders.get("/backOffice/guess/lottery/addNewGuess")
                .param("guessImg",guessImg)
                .param("guessType",guessType)
                .param("itemList",str)
                .param("showEndTime",showEndTime.toString())
                .param("showStartTime",showStartTime.toString())
                .param("wagerStartTime",wagerStartTime.toString())
                .param("wagerEndTime",wagerEndTime.toString())
                .param("subtitle",subtitle)
                .param("title",title)
                .param("windowImg",windowImg)
                .param("sort",sort);
        try {
            mockMvc.perform(req).andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertTestGuessData(){
        List<GuessItem> guessItem = new ArrayList<>();
        GuessItem item1 = new GuessItem();
        item1.optionContent = "test1";
        GuessItem item2 = new GuessItem();
        item2.optionContent = "test1";
        guessItem.add(item1);
        guessItem.add(item2);
        Guess guess = Guess.newInstance("testTitle", "testSubtitle", guessItem, Constant.GuessType.POLITICS, 1603856197177L,
                1603956197177L, 1603886197177L, 1603896197177L);
        guess.setState(Constant.GuessState.WAGER);
        guessDaoService.save(guess);
    }
}
