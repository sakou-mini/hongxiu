package com.donglaistd.jinli.database.entity.system;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document
public class LiveDomainConfig {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    @Indexed(unique = true)
    private Constant.LiveSourceLine line;
    @Field
    private List<String> domains = new ArrayList<>();

    public LiveDomainConfig() {
    }

    public LiveDomainConfig(Constant.LiveSourceLine line, List<String> domains) {
        this.line = line;
        this.domains = domains;
    }

    public static LiveDomainConfig newInstance(Constant.LiveSourceLine line, List<String> domains){
        return new LiveDomainConfig(line, domains);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Constant.LiveSourceLine getLine() {
        return line;
    }

    public void setLine(Constant.LiveSourceLine line) {
        this.line = line;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }
}
