package com.donglaistd.jinli.processors.handler.task;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.TaskBuilder;
import com.donglaistd.jinli.database.entity.Task;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.TaskProcess;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetTaskInfoListRequestHandler extends MessageHandler {
    @Autowired
    private TaskBuilder taskBuilder;
    @Autowired
    TaskProcess taskProcess;
    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.GetTaskInfoListRequest request = messageRequest.getGetTaskInfoListRequest();
        var reply = Jinli.JinliMessageReply.newBuilder();
        var getTaskInfoListReply = Jinli.GetTaskInfoListReply.newBuilder();
        List<Task> taskList = taskProcess.getOrInitTask(user.getId());
        if(request.getIsUpdate()){
            getTaskInfoListReply.setHasUpdate(taskProcess.updateUserTask(user.getId()));
        }
        getTaskInfoListReply.addAllTaskInfo(taskBuilder.toTaskInfo(taskList));
        reply.setGetTaskInfoListReply(getTaskInfoListReply);
        return reply.build();
    }
}
