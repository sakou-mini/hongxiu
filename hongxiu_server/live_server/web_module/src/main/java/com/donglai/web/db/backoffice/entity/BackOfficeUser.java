package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Document
@Data
@NoArgsConstructor
public class BackOfficeUser implements Serializable {
    @Id
    @AutoIncKey
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private String nickname;
    private String avatar = "";
    private String email;
    private String phone;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private long createTime;
    private long updateTime;
    @DBRef
    private List<Role> roles = new ArrayList<>();

    public BackOfficeUser(String username, String password, String nickname, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.roles = roles;
    }

    public static BackOfficeUser newInstance(String username, String password, String nickname, List<Role> role) {
        return new BackOfficeUser(username, password, nickname, role);
    }

    public static BackOfficeUser newEmptyBackOfficeUser() {
        return new BackOfficeUser();
    }

    public List<String> getRoleList() {
        return roles.stream().map(Role::getName).collect(Collectors.toList());
    }
}
