package com.donglaistd.jinli.database.entity.backoffice;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;


@Document
public class BackOfficeUser implements Serializable {
    @Field
    private Date createDate;
    @Id
    private String id;
    @Field
    @Indexed(unique = true)
    private String accountName;
    @Field
    private String token;
    @Field
    private Set<BackOfficeRole> role;
    public BackOfficeUser(String accountName, String token) {
        createDate = Calendar.getInstance().getTime();
        this.accountName = accountName;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Set<BackOfficeRole> getRoles() {
        return role;
    }

    public void setRole(Set<BackOfficeRole> role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackOfficeUser user = (BackOfficeUser) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
