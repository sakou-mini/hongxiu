package com.donglai.model.db.entity.live;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author Moon
 * @date 2022-02-14 10:51
 */
@Data
public class TaskLog {

    @Id
    @AutoIncKey
    private String id;

    /**
     * 用户绑定
     */
    private String userId;

    /**
     * 任务时间
     */
    private long taskDate;

    /**
     * 任务进度
     */
    private int taskSchedule;

    /**
     * 任务需要完成的总数
     */
    private int taskCount;

    /**
     * 任务类型
     */
    private Constant.TaskType taskEnum;

    /**
     * 任务积分奖励
     */
    private int taskIntegral;

    /**
     * 任务金币奖励
     */
    private int taskGold;

    /**
     * 是否完成
     */
    private boolean done;


    public Account.TaskLog toProto(){
        Account.TaskLog.Builder builder = Account.TaskLog.newBuilder();
        builder.setDone(done);
        builder.setTaskCount(taskCount);
        builder.setTaskEnum(taskEnum);
        builder.setTaskGold(taskGold);
        builder.setTaskIntegral(taskIntegral);
        builder.setTaskSchedule(taskSchedule);
        return builder.build();
    }
}
