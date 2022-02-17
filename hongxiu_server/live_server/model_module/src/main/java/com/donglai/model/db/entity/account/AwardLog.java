package com.donglai.model.db.entity.account;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @author Moon
 * @date 2022-02-14 10:57
 */
@Data
public class AwardLog {

    @Id
    @AutoIncKey
    private String id;

    /**
     * 0 积分   1金币
     */
    private int type;

    /**
     * 奖励金额
     */
    private int awardNum;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 创建时间
     */
    private long createdDate;


    public static AwardLog newInstance(int type, int awardNum, String userId, long createdDate){
        AwardLog awardLog = new AwardLog();
        awardLog.setType(type);
        awardLog.setAwardNum(awardNum);
        awardLog.setUserId(userId);
        awardLog.setCreatedDate(createdDate);
        return awardLog;
    }
}
