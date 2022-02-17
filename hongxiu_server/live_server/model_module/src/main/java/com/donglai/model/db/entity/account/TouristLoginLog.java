package com.donglai.model.db.entity.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * @author Moon
 * @date 2021-12-22 13:57
 */
@Data
@NoArgsConstructor
public class TouristLoginLog {
    @Id
    private ObjectId id = ObjectId.get();

    private String userId;

    private String ip;

    private Long createTime;

    public TouristLoginLog(String userId, String ip) {
        this.userId = userId;
        this.ip = ip;
        this.createTime = System.currentTimeMillis();
    }
}
