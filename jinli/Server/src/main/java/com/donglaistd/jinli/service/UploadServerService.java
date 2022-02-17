package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.HttpUtil;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.GameConstant.HTTP_URL;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;

@Component
public class UploadServerService {
    private Logger logger = Logger.getLogger(UploadServerService.class.getName());
    @Value("${upload.nodes}:")
    private final List<String> uploadServerNodes = new ArrayList<>();

    @Value("${upload.heart.connected.time}")
    private long heartTime;

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private Environment env;

    private String activeUpload;

    public UploadServerService( @Value("${upload.nodes}") List<String> uploadServerNodes) {
        if (uploadServerNodes.size() > 0) {
            this.activeUpload = uploadServerNodes.get(0);
        }
    }

    public void checkIsAlive(){
        if(uploadServerNodes.isEmpty()) return;
        int index = uploadServerNodes.indexOf(activeUpload);

        if(!httpUtil.checkHostIsLive(activeUpload)) {
            logger.warning("uploadServer Server cant connected! for : "+ activeUpload);
            if(index == uploadServerNodes.size()-1 ){
                activeUpload = uploadServerNodes.get(0);
            } else {
                activeUpload = uploadServerNodes.get(index+1);
            }
            /*logger.warning("set other activeUploadServer for:" + activeUpload);
            if(httpUtil.checkHostIsLive(activeUpload)) {
                broadResourceServerChangeURLBroadcastMessage();
            }*/
        }
        initConnected();
    }

    public String getActiveUpload() {
        return activeUpload;
    }

    public void initConnected(){
        ScheduledTaskUtil.schedule(this::checkIsAlive, heartTime);
    }

    public void broadResourceServerChangeURLBroadcastMessage(){
        String resourceServer = HTTP_URL + activeUpload;
        Jinli.ResourceServerChangeURLBroadcastMessage.Builder builder = Jinli.ResourceServerChangeURLBroadcastMessage.newBuilder().setResourceServer(resourceServer);
        logger.warning("upload server is auto change SUCCESS!");
        DataManager.userChannel.values().forEach(channel->{
            sendMessage(channel, buildReply(builder));
        });
    }
}
