package com.donglai.web.db.backoffice.entity;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

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
    private boolean status;
    private String roleAlias;
    private Date created;
    private Date updated;

    public Role(String name) {
        this.name = name;
        this.created = new Date(System.currentTimeMillis());
    }

    public static Role newInstance(String roleName) {
        return new Role(roleName);
    }
}
