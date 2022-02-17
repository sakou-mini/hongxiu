package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.backoffice.MenuRole;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class BackOfficeUserSend {
    public String id;

    public String accountName;

    public Date createDate;

    public Set<BackOfficeRole> role;

    public List<MenuRole> menuRoles;
}
