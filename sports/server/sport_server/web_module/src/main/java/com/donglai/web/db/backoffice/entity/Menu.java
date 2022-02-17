package com.donglai.web.db.backoffice.entity;

import com.donglai.web.constant.MenuType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

import static com.donglai.web.constant.WebConstant.MENU_TYPE_OF_GROUP;

@Data
@NoArgsConstructor
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Menu implements Serializable {
    @Id
    private String id = ObjectId.get().toString();
    @Indexed(unique = false,sparse = true)
    private String path;
    private String name;
    private String component;
    private String redirect;
    private String type;
    private String jurisdiction;
    private String jurisdictionType;
    private String icon;
    private String displayName;
    private String parentId;
    private String groupId;
    private MenuType menuType = MenuType.MENU_PAGE_SYSTEM;

    private boolean enabled = true;

    public Menu(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public static Menu newInstance(String path, String name){
        return new Menu(path, name);
    }

    public boolean notGroupType(){
        return !Objects.equals(MENU_TYPE_OF_GROUP, type);
    }

}
