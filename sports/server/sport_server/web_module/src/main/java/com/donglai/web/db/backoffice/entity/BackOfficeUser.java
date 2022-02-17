package com.donglai.web.db.backoffice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglai.web.constant.BackOfficeRole.ROLE_ADMIN;
import static com.donglai.web.constant.BackOfficeRole.ROLE_DUOCAI_PLATFORM;

@Document
@Data
@NoArgsConstructor
public class BackOfficeUser implements Serializable {
    @Id
    private String id = ObjectId.get().toString();
    @Indexed(unique = true)
    private String username;
    private String password;
    private String nickname;
    private String avatar = "";
    private boolean accountNonExpired = true;
    //账号被锁定（false 表示 该账号不可见，逻辑删除标志）
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private long createTime;
    private long updateTime;
    /*后台操作人id*/
    private String operator_id;
    @DBRef
    private List<Role> roles = new ArrayList<>();

    public BackOfficeUser(String username, String password, String nickname, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.roles = roles;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }

    public static BackOfficeUser newInstance(String username, String password, String nickname, List<Role> role) {
        return new BackOfficeUser(username, password, nickname, role);
    }

    public List<String> getRoleList(){
        return roles.stream().map(Role::getName).collect(Collectors.toList());
    }

    public boolean hasAdminRole(){
        return roles.stream().anyMatch(role -> Objects.equals(role.getName(), ROLE_ADMIN.name()));
    }

    public boolean hasAdminPlatformRole(){
        return roles.stream().anyMatch(role -> Objects.equals(role.getName(), ROLE_DUOCAI_PLATFORM.name()));
    }

    public void cleanRole(){
        roles.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackOfficeUser that = (BackOfficeUser) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
