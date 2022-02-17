package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

import static com.donglai.web.constant.BackOfficeRole.ROLE_ADMIN;
import static com.donglai.web.constant.BackOfficeRole.ROLE_DUOCAI_PLATFORM;

@Document
@NoArgsConstructor
@Data
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @AutoIncKey
    private String id;
    @Indexed(unique = true)
    private String name;
    private String roleAlias;
    private long created;
    private long updateTime;
    //是否启用
    private boolean enabled = true;
    /*后台操作人id*/
    private String operator_id;


    public Role(String name, String roleAlias) {
        this.name = name;
        this.roleAlias = roleAlias;
        this.created = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }

    public static Role newInstance(String roleName,String roleAlias) {
        return new Role(roleName,roleAlias);
    }

    @JsonIgnore
    public boolean isAdminRole(){
        return Objects.equals(name, ROLE_ADMIN.name());
    }

    @JsonIgnore
    public boolean isPlatformRole(){
        return Objects.equals(name, ROLE_DUOCAI_PLATFORM.name());
    }
}
