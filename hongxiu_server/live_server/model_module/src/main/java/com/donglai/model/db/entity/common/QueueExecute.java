package com.donglai.model.db.entity.common;

import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;


@Data
@NoArgsConstructor
@ToString
@Document
public class QueueExecute implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String LIVE = "live";
    public static final String BLOG = "blog";
    public static final String STATISTIC = "statistic";
    @Id
    private ObjectId id = ObjectId.get();

    //服务器源
    private String fromServer = LIVE;

    // 所属玩家
    private String uid;

    // 队列开始时间
    private long startTime;

    // 队列结束时间
    private long endTime;

    // 队列结束触发类型
    private int triggerType;

    // 排队队列属性索引
    private int queueId;

    /**
     * 额外附加属性
     */
    private String refId;

    /*额外附加状态属性*/
    private int refStatus;

    private Constant.PlatformType platform;

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        this.id = new ObjectId(id);
    }

    public QueueExecute(String uid, long startTime, long endTime, int triggerType, Constant.PlatformType platform) {
        this.uid = uid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.triggerType = triggerType;
        this.fromServer = LIVE;
        this.platform = platform;
    }

    public QueueExecute(String uid, long startTime, long endTime, int triggerType, String refId, Constant.PlatformType platform) {
        this.uid = uid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.triggerType = triggerType;
        this.refId = refId;
        this.fromServer = LIVE;
        this.platform = platform;
    }

    public QueueExecute(long startTime, long endTime, int triggerType, String refId, Constant.PlatformType platform) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.triggerType = triggerType;
        this.refId = refId;
        this.fromServer = LIVE;
        this.platform = platform;
    }

    public QueueExecute(long startTime, long endTime, int triggerType, Constant.PlatformType platform) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.triggerType = triggerType;
        this.fromServer = LIVE;
        this.platform = platform;
    }

}
