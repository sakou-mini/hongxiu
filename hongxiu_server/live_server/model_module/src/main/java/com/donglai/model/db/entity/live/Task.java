package com.donglai.model.db.entity.live;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.protocol.Constant;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author Moon
 * @date 2022-02-14 10:25
 */
@Data
public class Task {

    @Id
    @AutoIncKey
    private String id;

    /**
     * 任务需要完成的总数
     */
    private int taskCount;

    /**
     * 任务类型
     */
    private Constant.TaskType taskEnum;

    /**
     * vip等级
     */
    private int userLevel;

    /**
     * 任务积分奖励
     */
    private int taskIntegral;

    /**
     * 任务金币奖励
     */
    private int taskGold;

    /**
     * 任务最大完成的总数 (天数)
     */
    private int taskTotalDays;
}
