package com.donglai.model.db.entity.live;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
@NoArgsConstructor
/*CDN 直播线路配置*/
public class LiveDomain {
    @Id
    private int lineCode;
    @Field
    private String nodeName;
    @Field
    private boolean enable= true; // true 启用、false 禁用
    @Field
    private List<String> domains = new ArrayList<>();
    @Field
    private long updateTime;


    private LiveDomain(int lineCode, String nodeName,List<String> domains) {
        this.lineCode = lineCode;
        this.domains = domains;
        this.nodeName = nodeName;
        this.updateTime = System.currentTimeMillis();
    }

    public static LiveDomain newInstance(int lineCode,String nodeName, List<String> domains){
        return new LiveDomain(lineCode,nodeName, domains);
    }
}
