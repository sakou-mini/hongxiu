package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.http.controller.UploadController;
import com.donglaistd.jinli.util.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.InvocationTargetException;

import static com.donglaistd.jinli.Constant.UploadHandlerType.*;

public class AddBackgroundMusicTest extends BaseTest {
    @Autowired
    UploadController uploadController;
    private MockMvc mockMvc;
    @Before
    @Override
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(uploadController).build();
    }

    @Test
    public void addUserBackGroundMusicTest(){
        user.setOnline(true);
        dataManager.saveUser(user);
        String file_content_type = "audio/fac";
        String file_path = "/audio/0006291548";
        String userId = user.getId();
        String musicTitle = "悬崖上的金鱼姬";
        String musicSinger = "久石让";
        String musicTime = "6000000";
        String url = "/upload/uploadAudio?file_content_type=" + file_content_type +
                "&file_path=" + file_path +
                "&userId=" + userId +
                "&handlerType=" + MUSIC_VALUE +
                "&musicTitle=" + musicTitle +
                "&musicSinger=" + musicSinger +
                "&musicTime=" + musicTime;
        RequestBuilder httpRequest = MockMvcRequestBuilders.post(url);
        try {
            mockMvc.perform(httpRequest).andExpect(MockMvcResultMatchers.status().is(200));
            mockMvc.perform(httpRequest).andExpect(MockMvcResultMatchers.status().is(500)).andExpect(MockMvcResultMatchers.content().string("REPEATED_MUSIC"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
