package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Jinli;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.donglaistd.jinli.Constant.ResultCode.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatMessageRequestHandlerTest extends BaseTest {

    @Autowired
    ChatMessageRequestHandler chatMessageRequestHandler;

    @Test

    public void TestSuccessfulSendMessage() {
        var request = Jinli.JinliMessageRequest.newBuilder();
        var chatMessageRequest = Jinli.ChatMessageRequest.newBuilder();
        chatMessageRequest.setContent("a chat message");
        request.setChatMessageRequest(chatMessageRequest);

        var reply = chatMessageRequestHandler.handle(context, request.build() );
        Assert.assertEquals(SUCCESS, reply.getResultCode());
    }
    @Test

    public void TestSendMessageTooLong() {
        var request = Jinli.JinliMessageRequest.newBuilder();
        var chatMessageRequest = Jinli.ChatMessageRequest.newBuilder();
        chatMessageRequest.setContent("this is a long chat message ! just test,this is a long chat message ! just test," +
                "this is a long chat message ! just test,this is a long chat message ! just test,this is a long chat message ! just test ~~");
        request.setChatMessageRequest(chatMessageRequest);

        var reply = chatMessageRequestHandler.handle(context, request.build() );
        Assert.assertEquals(CHAT_MESSAGE_FORMAT_ERROR, reply.getResultCode());
    }
    @Test

    public void TestSendMessageTooSoon() {
        var request = Jinli.JinliMessageRequest.newBuilder();
        var chatMessageRequest = Jinli.ChatMessageRequest.newBuilder();
        chatMessageRequest.setContent("a chat message");
        request.setChatMessageRequest(chatMessageRequest);

        var reply = chatMessageRequestHandler.handle(context, request.build() );
        Assert.assertEquals(SUCCESS, reply.getResultCode());
        reply = chatMessageRequestHandler.handle(context, request.build() );
        Assert.assertEquals(SEND_CHAT_MESSAGE_TOO_SOON, reply.getResultCode());
    }
}
