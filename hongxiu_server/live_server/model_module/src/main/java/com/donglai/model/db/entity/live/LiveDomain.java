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
public class LiveDomain {
    @Id
    private int lineCode;
    @Field
    private List<String> domains = new ArrayList<>();


    private LiveDomain(int lineCode, List<String> domains) {
        this.lineCode = lineCode;
        this.domains = domains;
    }

    public static LiveDomain newInstance(int lineCode, List<String> domains) {
        return new LiveDomain(lineCode, domains);
    }
}
