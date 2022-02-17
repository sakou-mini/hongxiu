package com.donglai.web.web.vo;

import com.donglai.web.db.backoffice.entity.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Moon
 * @date 2021-12-29 10:40
 */
@Data
public class BackOfficeUserVO {

    private String id;

    private String nickname;

    private String roleName;

    private Boolean enabled;

    private Long createTime;

    private Long updateTime;

    private List<Role> roles = new ArrayList<>();
}
