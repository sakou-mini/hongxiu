package com.donglai.web.web.dto.reply;

import com.donglai.web.db.backoffice.entity.Role;
import lombok.Data;

@Data
public class RoleItemReply {
    private String roleId;
    private String roleAlias;

    public RoleItemReply(String roleId, String roleAlias) {
        this.roleId = roleId;
        this.roleAlias = roleAlias;
    }

    public static RoleItemReply newInstance(Role role){
        return new RoleItemReply(role.getId(), role.getRoleAlias());
    }
}
