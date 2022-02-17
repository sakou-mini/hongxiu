package com.donglai.model.db.entity.live;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BulletMessage implements Serializable {
    private String content;
    private String sendUserId;
    private String roomId;
}
