package com.donglai.web.dto;

import lombok.Data;
@Data
public class PermissionJSONConfig {
    /*菜单id*/
    private String menuId;
    /*权限名*/
    private String name;
    /*权限标记*/
    private String auth;
    /*Api路径*/
    private String apiPath;

}
