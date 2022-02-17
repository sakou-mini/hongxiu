package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author Moon
 * @date 2021-12-30 11:46
 */
@Data
@Document
public class BackOfficeLog {
    @Id
    @AutoIncKey
    private Long id;

    private String interfaceName;

    private String logTxt;

    private String createdName;

    private String createdIp;

    private Date createdTime;

    private Boolean res;

    private String createdId;

    private String url;


}
