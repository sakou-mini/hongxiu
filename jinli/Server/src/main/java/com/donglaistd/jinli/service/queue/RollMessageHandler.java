package com.donglaistd.jinli.service.queue;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.QueueExecuteDaoService;
import com.donglaistd.jinli.database.dao.system.SystemMessageConfigDaoService;
import com.donglaistd.jinli.database.entity.QueueExecute;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import com.donglaistd.jinli.service.QueueProcess;
import com.donglaistd.jinli.service.SystemMessageConfigProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RollMessageHandler implements TriggerHandler{
    @Autowired
    SystemMessageConfigProcess systemMessageConfigProcess;
    @Autowired
    SystemMessageConfigDaoService systemMessageConfigDaoService;
    @Autowired
    QueueExecuteDaoService queueExecuteDaoService;
    @Autowired
    QueueProcess queueProcess;

    @Override
    public void deal(QueueExecute queueExecute) {
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(Integer.parseInt(queueExecute.getRefId()));
        if(platformType == null) return;
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(platformType);
        if(systemMessageConfig == null) return;
        if(systemMessageConfig.getRollDisPlayEndTime() <= 0 || systemMessageConfig.rollMessageIsExpire()) return;
        systemMessageConfigProcess.sendSystemRollMessage(systemMessageConfig);
        queueProcess.addRollQueue(systemMessageConfig);
    }
}
