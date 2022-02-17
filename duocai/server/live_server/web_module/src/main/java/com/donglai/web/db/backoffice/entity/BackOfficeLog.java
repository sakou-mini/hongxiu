package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

/**
 * @author yty
 * @date 2021-12-30 11:46
 */
@Data
@Document
public class BackOfficeLog {
    @Id
    @AutoIncKey
    private long id;
    /*接口名， 模块描述*/
    private String interfaceName;
    /*日志描述，操作参数*/
    private String logTxt;
    /*后台 操作人 昵称 + 用户名*/
    private String createdName;
    /*操作人 ip*/
    private String createdIp;
    /*创建日期*/
    private long createdTime;
    /*操作结果*/
    private boolean res;
    /*操作状态码*/
    private String resDesc;
    /*后台人员id*/
    private String createdId;
    /*请求路径url*/
    private String url;
}
