package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class RoomManagementRequestHandler extends MessageHandler implements ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(RoomManagementRequestHandler.class.getName());

    private final Map<String, RoomManagementHandler> messageHandlerMap = new HashMap<>();

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        RoomManagement.RoomManagementRequest request = messageRequest.getRoomManagementRequest();
        RoomManagementHandler roomManagementHandler = messageHandlerMap.get(request.getRequestCase().toString());
        String roomId = request.getRoomId();
        Room room = DataManager.roomMap.get(roomId);
        if (Objects.isNull(room)) {
            logger.warning("room is null for request" + roomManagementHandler.getClass());
            return buildReply(RoomManagement.RoomManagementReply.newBuilder(), Constant.ResultCode.ROOM_DOES_NOT_EXIST);
        }
        var pair = roomManagementHandler.handle(ctx, messageRequest.getRoomManagementRequest(), user, room);
        return buildReply(pair.getLeft(), pair.getRight());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, RoomManagementHandler> beans = applicationContext.getBeansOfType(RoomManagementHandler.class);
        beans.forEach((k, v) -> {
            messageHandlerMap.put(k.replace("Handler", "").toUpperCase(), v);
        });
    }
}
