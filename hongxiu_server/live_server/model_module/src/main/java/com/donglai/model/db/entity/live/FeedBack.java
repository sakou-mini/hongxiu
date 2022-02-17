package com.donglai.model.db.entity.live;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

/**
 * @author Moon
 * @date 2021-12-28 16:23
 */
@Data
@NoArgsConstructor
public class FeedBack {

    @Id
    @AutoIncKey
    private Long id;

    private String content;

    private Integer type;

    private String createdId;

    private String phone;

    private Long createdTime;

    private String createdIp;

    private Integer status;

    private String reply;

    public FeedBack(String content, Integer type, String createdId, String phone, String createdIp, Integer status) {
        this.content = content;
        this.type = type;
        this.createdId = createdId;
        this.phone = phone;
        this.createdTime = System.currentTimeMillis();
        this.createdIp = createdIp;
        this.status = status;
        this.reply = reply;
    }
}
