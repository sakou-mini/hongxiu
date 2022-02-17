package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

import static com.donglai.web.constant.ShiroSecurityConstant.AUTH_PERMISSION;

@Document
@NoArgsConstructor
@Data
public class Permission {
    @Id
    @AutoIncKey
    private String id;
    /*权限名*/
    private String name;
    /*权限标记*/
    private String auth;
    /*创建时间*/
    private long createTime = System.currentTimeMillis();
    /*更新时间*/
    private long updateTime = System.currentTimeMillis();
    /*后台操作人id*/
    private String operator_id;
    /*Api路径*/
    @Indexed(unique = true)
    private String apiPath;

    @JsonIgnore
    public String getFormatAuth() {
        return AUTH_PERMISSION + "[" + auth + "]";
    }

}
