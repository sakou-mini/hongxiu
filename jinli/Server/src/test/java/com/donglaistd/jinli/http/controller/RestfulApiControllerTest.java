package com.donglaistd.jinli.http.controller;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.http.controller.api.RestfulApiController;
import com.donglaistd.jinli.processors.handler.LoginRequestHandler;
import com.donglaistd.jinli.service.api.RestfulApiService;
import com.donglaistd.jinli.util.OrderIdUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestfulApiControllerTest extends BaseTest {

    @Autowired
    private RestfulApiController controller;
    @Autowired
    RestfulApiService restfulApiService;
    private MockMvc mockMvc;

    @Autowired
    private LoginRequestHandler loginRequestHandler;


    @Before
    @Override
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void TestCanGetTokenAndExpire() throws Exception {
        restfulApiService.tokenExpireTimeMilliseconds = 5000;
        String url = "/api/v1/requestToken?accountName=2e12";
        var mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("platformA");
        RequestBuilder request = MockMvcRequestBuilders.post(url).principal(mockPrincipal);
        var result = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        var objectMapper = new ObjectMapper();
        var node = objectMapper.readTree(result);
        Assert.assertEquals(0, node.get("result").asInt());
        Assert.assertEquals("success", node.get("msg").asText());
        Assert.assertEquals("2e12", node.get("accountName").asText());
        Assert.assertEquals("platformA", node.get("platformName").asText());
        var result2 = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(result, result2);
        Thread.sleep(6000);
        var result3 = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        Assert.assertNotEquals(result, result3);
    }

    @Test
    public void testLoginAfterRequestToken() throws Exception {
        String url = "/api/v1/requestToken?accountName=anyname";
        var mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("platformName");
        RequestBuilder request = MockMvcRequestBuilders.post(url).principal(mockPrincipal);
        var result = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        var objectMapper = new ObjectMapper();
        var node = objectMapper.readTree(result);
        var token = node.get("token").asText();
        var loginRequest = Jinli.JinliMessageRequest.newBuilder().setLoginRequest(Jinli.LoginRequest.newBuilder().setAccount("platformName~anyname").setPassword(token));
        var reply = loginRequestHandler.handle(context, loginRequest.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());
    }

    @Test
    public void testMultiThreadRequestToken() throws Exception {
        Set<String> tokenList = new ConcurrentHashSet<>();
        for (int i = 0; i <100 ; i++) {
            new Thread(()->{
                var mockPrincipal = Mockito.mock(Principal.class);
                String url = "/api/v1/requestToken?accountName="+ OrderIdUtils.getOrderNumber()+"&disPlatName=VIP"+OrderIdUtils.getOrderNumber();
                Mockito.when(mockPrincipal.getName()).thenReturn("platformAccount");
                RequestBuilder request = MockMvcRequestBuilders.post(url).principal(mockPrincipal);
                String result = null;
                try {
                    result = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                var objectMapper = new ObjectMapper();
                JsonNode node = null;
                try {
                    node = objectMapper.readTree(result);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                String token = node.get("token").asText();
                System.out.println(Thread.currentThread().getName() + "---->"+token);
                tokenList.add(token);
            }).start();

        }
        Thread.sleep(6000);
        Assert.assertEquals(100,tokenList.size());
        //获取直播地址
        Set<String> liveUrls = new ConcurrentHashSet<>();
        for (String token : tokenList) {
            String url = String.format("/api/v1/live?token=%s", token);
            var mockPrincipal = Mockito.mock(Principal.class);
            Mockito.when(mockPrincipal.getName()).thenReturn("platformAccount");
            RequestBuilder request = MockMvcRequestBuilders.post(url).principal(mockPrincipal);
            var result = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
            var objectMapper = new ObjectMapper();
            var node = objectMapper.readTree(result);
            String liveUrlList = node.get("liveUrlList").get(0).asText();
            liveUrls.add(liveUrlList);
        }
        Assert.assertEquals(100,liveUrls.size());
        System.out.println(liveUrls.size());
    }
}
