package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.CarouselBuilder;
import com.donglaistd.jinli.constant.QueueType;
import com.donglaistd.jinli.database.dao.BackOfficeUserDaoService;
import com.donglaistd.jinli.database.dao.QueueExecuteDaoService;
import com.donglaistd.jinli.database.dao.backoffice.ChangeRollMessageRecordDaoService;
import com.donglaistd.jinli.database.dao.backoffice.ChangeSystemTipMessageRecordDaoService;
import com.donglaistd.jinli.database.dao.system.SystemMessageConfigDaoService;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.backoffice.ChangeRollMessageRecord;
import com.donglaistd.jinli.database.entity.backoffice.ChangeSystemTipMessageRecord;
import com.donglaistd.jinli.database.entity.race.RaceBase;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import com.donglaistd.jinli.database.entity.system.carousel.BaseCarousel;
import com.donglaistd.jinli.http.entity.CarouselRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class SystemMessageConfigProcess {
    private  static final Logger logger = Logger.getLogger(SystemMessageConfigProcess.class.getName());
    @Autowired
    SystemMessageConfigDaoService systemMessageConfigDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    CarouselBuilder carouselBuilder;
    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;
    @Autowired
    ChangeRollMessageRecordDaoService changeRollMessageRecordDaoService;
    @Autowired
    ChangeSystemTipMessageRecordDaoService changeSystemTipMessageRecordDaoService;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;
    @Autowired
    QueueExecuteDaoService queueExecuteDaoService;
    @Autowired
    QueueProcess queueProcess;

    public void sendSystemRollMessage(SystemMessageConfig finalSystemMessageConfig) {
        if (finalSystemMessageConfig.rollMessageIsExpire()) {
            logger.info("execute RollMessageTask is Expire,stop execute");
            return;
        }
        if (checkRollMessageActiveTime(finalSystemMessageConfig)) {
            Constant.PlatformType platformType = finalSystemMessageConfig.getPlatformType();
            logger.info("发送滚动消息给客户端：" + finalSystemMessageConfig.getRollMessage() + " 平台：" + finalSystemMessageConfig.getPlatformType());
            DataManager.getOnlineRoomList().stream().filter(room -> Objects.equals(platformType,dataManager.findLiveUser(room.getLiveUserId()).getPlatformType()))
                    .forEach(room -> room.broadCastToAllPlatform(buildReply(buildSystemMessageConfigBroadcast(finalSystemMessageConfig.getRollMessage()))));
        }
    }

    private boolean checkRollMessageActiveTime(SystemMessageConfig systemMessageConfig){
        long now = System.currentTimeMillis();
        return now >= systemMessageConfig.getRollDisPlayStartTime() && now <= systemMessageConfig.getRollDisPlayEndTime();
    }

    public Jinli.SystemRollMessageBroadcastMessage buildSystemMessageConfigBroadcast(String rollMessage){
        return Jinli.SystemRollMessageBroadcastMessage.newBuilder().setSystemRollMessage(rollMessage).build();
    }

    public void updateSystemRollMessageConfig(String message,long intervalTime,long startTime,long endTime,Constant.PlatformType platformType,String backofficeAccountName){
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(platformType);
        String oldRollMessage = systemMessageConfig.getRollMessage();
        systemMessageConfig.setRollMessage(message);
        systemMessageConfig.setRollIntervalTime(intervalTime);
        systemMessageConfig.setRollDisPlayStartTime(startTime);
        systemMessageConfig.setRollDisPlayEndTime(endTime);
        systemMessageConfigDaoService.save(systemMessageConfig);
        recordUpdateSystemRollMessage(backofficeAccountName, oldRollMessage, systemMessageConfig);
        cleanRollQueue(platformType);
        sendSystemRollMessage(systemMessageConfig);
        queueProcess.addRollQueue(systemMessageConfig);
    }

    private void cleanRollQueue(Constant.PlatformType platformType) {
        queueExecuteDaoService.deleteByRefIdAndTriggerType(QueueType.SEND_ROLL_MESSAGE.getValue(),String.valueOf(platformType.getNumber()));
    }

    public void recordUpdateSystemRollMessage(String backofficeUserName,String oldMessage,SystemMessageConfig newMessageConfig){
        BackOfficeUser backOfficeUser = backOfficeUserDaoService.findByAccountName(backofficeUserName);
        if(backOfficeUser!=null){
            ChangeRollMessageRecord changeRollMessageRecord = new ChangeRollMessageRecord(oldMessage, newMessageConfig, backOfficeUser.getId());
            changeRollMessageRecordDaoService.save(changeRollMessageRecord);
        }
    }

    public void setTipsMessage(Constant.PlatformType platformType, String message,String backofficeAccountName){
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(platformType);
        String oldTipMessage = systemMessageConfig.getSystemTipMessage();
        systemMessageConfig.setSystemTipMessage(message);
        systemMessageConfigDaoService.save(systemMessageConfig);
        recordUpdateSystemTipMessage(backofficeAccountName, oldTipMessage, systemMessageConfig);
    }

    public void recordUpdateSystemTipMessage(String backofficeUserName,String oldMessage,SystemMessageConfig newMessageConfig){
        BackOfficeUser backOfficeUser = backOfficeUserDaoService.findByAccountName(backofficeUserName);
        if(backOfficeUser!=null){
            ChangeSystemTipMessageRecord changeSystemTipMessageRecord = new ChangeSystemTipMessageRecord(oldMessage, newMessageConfig, backOfficeUser.getId());
            changeSystemTipMessageRecordDaoService.save(changeSystemTipMessageRecord);
        }
    }


    public List<Integer> getRacesFreeByRaceType(Constant.RaceType raceType){
        return DataManager.getAllRaceByRaceType(raceType).stream().map(RaceBase::getRaceFee)
                .sorted(Comparator.comparing(Integer::intValue)).collect(Collectors.toList());
    }

    public Constant.ResultCode createCarousel(CarouselRequest request){
        return carouselBuilder.create(request).getLeft();
    }

    public  List<? extends BaseCarousel> getCarouselByType(Constant.CarouselType carouselType){
        return carouselBuilder.getCarouselListByType(carouselType);
    }

    public Constant.ResultCode updateCarousel(CarouselRequest request) {
        return carouselBuilder.updateCarouselData(request);
    }

    public Constant.ResultCode removeCarousel(String id, Constant.CarouselType carouselType) {
        return  carouselBuilder.removeCarousel(id, carouselType);
    }

    public BaseCarousel findByIdAndCarouselType(String id, Constant.CarouselType carouselType){
        return carouselBuilder.findById(id, carouselType);
    }

    public PageInfo<ChangeRollMessageRecord> findChangeRollMessageRecord(int page, int size, Constant.PlatformType platform) {
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(platform);
        Page<ChangeRollMessageRecord> pageInfo = changeRollMessageRecordDaoService.queryChangeRollMessageRecordByPlatform(page, size, platform);
        List<ChangeRollMessageRecord> content = pageInfo.getContent();
        BackOfficeUser backOfficeUser;
        for (ChangeRollMessageRecord record : content) {
            backOfficeUser = backOfficeUserDaoService.findById(record.getBackOfficeUserId());
            if(backOfficeUser!=null){
                record.setBackOfficeAccountName(backOfficeUser.getAccountName());
                record.setTakeEffect(rollMessageRecordIsEffect(systemMessageConfig,record));
            }
        }
        return new PageInfo<>(content, pageInfo.getTotalElements());
    }

    private boolean rollMessageRecordIsEffect(SystemMessageConfig systemMessageConfig, ChangeRollMessageRecord record){
        if(systemMessageConfig == null) return false;
        return Objects.equals(systemMessageConfig.getRollMessage(), record.getNewMessage())
                && Objects.equals(systemMessageConfig.getRollDisPlayStartTime(), record.getRollDisPlayStartTime())
                && Objects.equals(systemMessageConfig.getRollDisPlayEndTime(), record.getRollDisPlayEndTime());
    }

    public PageInfo<ChangeSystemTipMessageRecord> findChangeSystemTipMessageRecord(int page, int size, Constant.PlatformType platform) {
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(platform);
        Page<ChangeSystemTipMessageRecord> pageInfo = changeSystemTipMessageRecordDaoService.queryChangeSystemTipMessageRecordByPlatform(page, size, platform);
        List<ChangeSystemTipMessageRecord> content = pageInfo.getContent();
        BackOfficeUser backOfficeUser;
        for (ChangeSystemTipMessageRecord record : content) {
            backOfficeUser = backOfficeUserDaoService.findById(record.getBackOfficeUserId());
            if(backOfficeUser!=null){
                record.setBackOfficeAccountName(backOfficeUser.getAccountName());
                record.setTakeEffect(systemTipMessageRecordIsEffect(systemMessageConfig,record));
            }
        }
        return new PageInfo<>(content, pageInfo.getTotalElements());
    }

    private boolean systemTipMessageRecordIsEffect(SystemMessageConfig systemMessageConfig, ChangeSystemTipMessageRecord record){
        if(systemMessageConfig == null) return false;
        return Objects.equals(systemMessageConfig.getSystemTipMessage(), record.getNewMessage());
    }

    public void cleanRollMessage(Constant.PlatformType platform, String backOfficeAccountName) {
        SystemMessageConfig message = systemMessageConfigDaoService.findSystemMessage(platform);
        String oldMessage = message.getRollMessage();
        if(message!=null){
            message.cleanRollMessage();
            systemMessageConfigDaoService.save(message);
            recordUpdateSystemRollMessage(backOfficeAccountName,oldMessage,message);
        }
    }
}
