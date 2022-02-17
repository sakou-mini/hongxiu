package com.donglaistd.jinli.processors;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameMessageDispatcherTest extends BaseTest implements ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(GameMessageDispatcherTest.class.getName());

    private ApplicationContext applicationContext;

    @Autowired
    GameMessageDispatcher gameMessageDispatcher;

    @Test
    public void HandlerAmountCheckTest() {
        var request = Jinli.JinliMessageRequest.newBuilder().build();
        var methods = request.getClass().getDeclaredMethods();
        var methodNames = Arrays.stream(methods).filter(m -> m.getName().matches("get.+Request")).map(name -> name.getName().replace("get", ""))
                .collect(Collectors.toSet());
        var beansNames = applicationContext.getBeansOfType(MessageHandler.class).keySet();
        methodNames.stream().filter(name -> isInclude(beansNames, name)).forEach(name -> logger.fine("method not found:" + name));
        Assert.assertEquals(methodNames.size(), beansNames.size());
    }

    private boolean isInclude(Set<String> methodNames, String name) {
        for (var methodName : methodNames) {
            if (methodName.replace("Handler", "").equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void GameDispatcherTestLoginMessage() {
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        builder.setLoginRequest(Jinli.LoginRequest.newBuilder().setAccount("account").setPassword("password").build());
        gameMessageDispatcher.dispatch(context, builder.build());
        var reply = Jinli.JinliMessageReply.newBuilder();
        reply.setResultCode(Constant.ResultCode.USER_NOT_FOUND);
        reply.setLoginReply(Jinli.LoginReply.newBuilder().setUserInfo(Jinli.UserInfo.newBuilder()));
        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(Unpooled.copiedBuffer(reply.build().toByteArray()));
        Mockito.verify(context).writeAndFlush(binaryWebSocketFrame);
    }

    @Test
    public void GameDispatcherTestWithEmptyMessageCase() {
        gameMessageDispatcher.dispatch(context, Jinli.JinliMessageRequest.newBuilder().build());
        var reply = Jinli.JinliMessageReply.newBuilder();
        reply.setResultCode(Constant.ResultCode.UNKNOWN);
        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(Unpooled.copiedBuffer(reply.build().toByteArray()));
        Mockito.verify(context).writeAndFlush(binaryWebSocketFrame);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
