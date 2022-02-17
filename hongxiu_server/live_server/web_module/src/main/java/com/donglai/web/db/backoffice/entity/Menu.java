package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.web.constant.MenuType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.web.constant.ShiroSecurityConstant.AUTH_PERMISSION;

@Data
@NoArgsConstructor
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Menu implements Serializable {
    @Id
    @AutoIncKey
    private String id;

    private String name;

    private String icon;

    private String redirect; //重定向页面

    @Indexed(unique = true, sparse = true)
    private String path;

    private MenuType menuType;

    private String parentId;

    private boolean enabled = true;
    @DBRef
    private List<Role> roles = new ArrayList<>();

    @Transient
    private List<Menu> children = new ArrayList<>();

    private int sortId;

    public Menu(String path, String name, MenuType menuType) {
        this.path = path;
        this.name = name;
        this.menuType = menuType;
    }

    public static Menu newInstance(String path, String name, MenuType menuType) {
        return new Menu(path, name, menuType);
    }

    @JsonIgnore
    public String[] getStrRolesArray() {
        return roles.stream().map(Role::getName).toArray(String[]::new);
    }

    @JsonIgnore
    public String getPermissionStr() {
        String permission = roles.stream().map(Role::getName).collect(Collectors.joining("|"));
        return AUTH_PERMISSION + "[" + permission + "]";
    }


    public void addRole(Role role) {
        if (!roles.contains(role)) {
            this.roles.add(role);
        }
    }

    public void addChildrenMenu(Menu menu) {
        children.add(menu);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }
}
