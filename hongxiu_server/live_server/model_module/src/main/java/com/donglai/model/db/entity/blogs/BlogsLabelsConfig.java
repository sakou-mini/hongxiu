package com.donglai.model.db.entity.blogs;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.model.constant.LabelType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Document
@NoArgsConstructor
public class BlogsLabelsConfig {
    @Id
    @AutoIncKey
    private int id;
    /*标签名*/
    @Indexed(unique = true)
    private String labelName;
    /*创建时间*/
    private long createTime;
    /*更新时间*/
    private long updateTime;
    /*后台操作人员*/
    private String backstageUserId;
    //标签类型 0 系统标签  1 自定义标签
    /**
     * 0 系统标签  1 自定义标签
     */
    private int type = LabelType.SYSTEM_LABEL.value;
    /*标签是否启用*/
    private boolean enable = true;


    public BlogsLabelsConfig(Integer id, String labelName, int type) {
        this.id = id;
        this.labelName = labelName;
        this.type = type;
        this.createTime = System.currentTimeMillis();
    }

    public BlogsLabelsConfig(String labelName, int type) {
        this.labelName = labelName;
        this.type = type;
        this.createTime = System.currentTimeMillis();
    }

    public void operationLabel(boolean enable, String backofficeUserId) {
        this.enable = enable;
        this.backstageUserId = backofficeUserId;
        this.updateTime = System.currentTimeMillis();
    }
}
