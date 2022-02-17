package com.donglai.web.web.dto.reply;

import com.donglai.common.util.CombineBeansUtil;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BackOfficeUserDto {
    private String id;
    private String nickname;
    private String roleName;
    private Boolean enabled;
    private Long createTime;
    private Long updateTime;
    private String username;
    private String avatar = "";
    private List<Role> roles = new ArrayList<>();
    private String operatorName;
}
