package com.donglaistd.jinli.database.entity.system;

import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Menu implements Serializable {
    private String url;
    private String name;
    private String displayName;
    private List<Menu> sidebar;
    private String childName;
    private List<Menu> child;
    private List<BackOfficeRole> role = new ArrayList<>();


    @JsonCreator
    public Menu(@JsonProperty("url") String url, @JsonProperty("name")  String name,
                @JsonProperty("displayName") String displayName, @JsonProperty("sidebar") List<Menu> sidebar,
                @JsonProperty("childName") String childName, @JsonProperty("child") List<Menu> child, @JsonProperty("role") String role) {
        this.url = url;
        this.name = name;
        this.displayName = displayName;
        this.sidebar = sidebar;
        this.childName = childName;
        this.child = child;
        if(!StringUtils.isNullOrBlank(role)){
            BackOfficeRole backOfficeRole = BackOfficeRole.valueOf(role);
            if(backOfficeRole!=null) this.role.add(backOfficeRole);
        }
    }

    public Menu() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Menu> getSidebar() {
        return sidebar;
    }

    public void setSidebar(List<Menu> sidebar) {
        this.sidebar = sidebar;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public List<Menu> getChild() {
        return child;
    }

    public void setChild(List<Menu> child) {
        this.child = child;
    }

    public List<BackOfficeRole> getRole() {
        return role;
    }

    public void setRole(List<BackOfficeRole> role) {
        this.role = role;
    }

    public void addRole(BackOfficeRole role){
        if(!this.role.contains(role))
            this.role.add(role);
    }

    public void addAllRole(List<BackOfficeRole> roles){
        for (BackOfficeRole role : roles) {
            addRole(role);
        }
    }

    @Override
    public String toString() {
        return "Menu{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", sidebar=" + sidebar +
                ", childName='" + childName + '\'' +
                ", child=" + child +
                ", role=" + role +
                '}';
    }
}
