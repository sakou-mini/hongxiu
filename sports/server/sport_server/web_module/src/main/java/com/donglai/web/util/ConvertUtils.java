package com.donglai.web.util;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.util.CombineBeansUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.web.dto.reply.BackOfficeUserDto;
import com.donglai.web.web.dto.reply.RoleListReply;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-12-21 14:29
 */
public class ConvertUtils {

   public static final BackOfficeUserService backOfficeUserService = SpringApplicationContext.getBean(BackOfficeUserService.class);

    public static BackOfficeUserDto backOfficeUserToBackOfficeUserVO(BackOfficeUser content) {
        CombineBeansUtil<BackOfficeUserDto> combineBeansUtil = new CombineBeansUtil<>(BackOfficeUserDto.class);
        BackOfficeUserDto backOfficeUserDto = combineBeansUtil.combineBeans(content);
        if(backOfficeUserDto != null && !StringUtils.isNullOrBlank(content.getOperator_id())) {
            BackOfficeUser operator = backOfficeUserService.findById(content.getOperator_id());
            if(Objects.nonNull(operator)){
                backOfficeUserDto.setOperatorName(operator.getNickname());
            }
        }

        return backOfficeUserDto;
    }

    public static List<BackOfficeUserDto> backOfficeUserListToBackOfficeUserVOList(List<BackOfficeUser> content) {
        List<BackOfficeUserDto> res = new ArrayList<>();
        for (BackOfficeUser backOfficeUser : content) {
            res.add(backOfficeUserToBackOfficeUserVO(backOfficeUser));
        }
        return res;
    }

    public static RoleListReply roleToRoleListReply(Role content) {
        CombineBeansUtil<RoleListReply> combineBeansUtil = new CombineBeansUtil<>(RoleListReply.class);
        RoleListReply roleListReply = combineBeansUtil.combineBeans(content);
        if(roleListReply != null && !StringUtils.isNullOrBlank(content.getOperator_id())) {
            BackOfficeUser operator = backOfficeUserService.findById(content.getOperator_id());
            if(Objects.nonNull(operator)){
                roleListReply.setOperatorName(operator.getNickname());
            }
        }
        return roleListReply;
    }

    public static List<RoleListReply> roleListToRoleListReplyList(List<Role> roleList) {
        List<RoleListReply> res = new ArrayList<>();
        for (Role role : roleList) {
            res.add(roleToRoleListReply(role));
        }
        return res;
    }
}
