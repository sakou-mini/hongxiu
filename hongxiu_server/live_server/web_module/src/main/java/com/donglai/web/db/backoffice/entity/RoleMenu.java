package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
@CompoundIndex(def = "{'roleId':1,'menuId':1}", unique = true)
public class RoleMenu {
    @AutoIncKey
    private long id;
    private String roleId;
    private String menuId;

    public RoleMenu(String roleId, String menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }

    public static RoleMenu newInstance(String roleId, String menuId) {
        return new RoleMenu(roleId, menuId);
    }
}

