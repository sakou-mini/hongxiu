package com.donglai.web.web.dto.reply;

import lombok.Data;
@Data
public class RoleListReply {
    //角色id
    private String id;
    //角色名
    private String name;
    //角色别名
    private String roleAlias;
    //创建时间
    private long created;
    //更新时间
    private long updateTime;
    //是否启用
    private boolean enabled = true;
    //操作人昵称
    private String operatorName;
}
