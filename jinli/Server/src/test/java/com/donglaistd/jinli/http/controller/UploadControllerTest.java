package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.zone.CreatePersonDiaryRequestHandler;
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

import static com.donglaistd.jinli.Constant.LiveStatus.OFFLINE;
import static com.donglaistd.jinli.Constant.LiveStatus.ONLINE;
import static com.donglaistd.jinli.Constant.UploadHandlerType.DIARY_VALUE;

public class UploadControllerTest extends BaseTest {
    @Autowired
    UploadController uploadController;
    @Autowired
    CreatePersonDiaryRequestHandler createPersonDiaryRequestHandler;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;

    private MockMvc mockMvc;

    @Before
    @Override
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(uploadController).build();
    }

    @Test
    public void upLoadImageTest() throws Exception {
        User user1 = createUserAndLiveUserTest();
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder().setCreatePersonDiaryRequest(Jinli.CreatePersonDiaryRequest.newBuilder()
                .setType(Constant.DiaryType.IMAGE).setContent("LLL").setTargetNum(9));
        Jinli.CreatePersonDiaryReply reply = createPersonDiaryRequestHandler.doHandle(context, request.build(), user1).getCreatePersonDiaryReply();
        String file_content_type = "image/";
        String file_path = "/user/tmp/100155";
        String userId = user1.getId();
        String diaryId = reply.getDiaryId();
        for (int i = 0; i <10 ; i++) {
            String url = "/upload/uploadImage?file_content_type=" + file_content_type + "&file_path=" + file_path + "&userId=" + userId + "&handlerType=" + DIARY_VALUE + "&diaryId=" + diaryId;
            RequestBuilder httpRequest = MockMvcRequestBuilders.post(url);
            if(i==9){
                mockMvc.perform(httpRequest).andExpect(MockMvcResultMatchers.status().is(500));
            }else{
                mockMvc.perform(httpRequest).andExpect(MockMvcResultMatchers.status().isOk());
            }
        }
    }

    private User  createUserAndLiveUserTest(){
        User user1 = creteUser("zsf1", 5000, 20);
        user1.setOnline(true);
        LiveUser liveUser = liveUserBuilder.create(user1.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        Room room = roomBuilder.create(liveUser.getId(), user1.getId(), "title", "desc", "");
        user1.setLiveUserId(liveUser.getId());
        dataManager.saveRoom(room);
        dataManager.saveUser(user1);
        liveUser.setRoomId(room.getId());
        liveUserDaoService.save(liveUser);
        return user1;
    }

    private User creteUser(String name, int coin, int id) {
        User user1 = userBuilder.createUser(name + id, name + id, "admin" + id);
        user1.setGameCoin(coin);
        return user1;
    }
}
