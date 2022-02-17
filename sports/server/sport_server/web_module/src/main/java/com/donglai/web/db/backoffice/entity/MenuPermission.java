package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

/*菜单权限中间表*/
@Document
@Data
@CompoundIndex(def = "{'menuId':1,'permissionId':1}", unique = true)
public class MenuPermission {
    @Id
    @AutoIncKey
    private String id;

    public String menuId;

    public String permissionId;

    public MenuPermission(String menuId, String permissionId) {
        this.menuId = menuId;
        this.permissionId = permissionId;
    }

    public static MenuPermission newInstance(String menuId, String permissionId){
        return new MenuPermission(menuId, permissionId);
    }
}
