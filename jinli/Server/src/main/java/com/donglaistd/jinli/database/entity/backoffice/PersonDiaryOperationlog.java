package com.donglaistd.jinli.database.entity.backoffice;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class PersonDiaryOperationlog {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private Date operationDate;
    @Field
    private String backOfficeId;
    @Field
    @Indexed(unique = true)
    private String personDiaryId;
    @Field
    private String rejectContent;
    @Field
    private boolean isApproval;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getBackOfficeId() {
        return backOfficeId;
    }

    public void setBackOfficeId(String backOfficeId) {
        this.backOfficeId = backOfficeId;
    }

    public String getPersonDiaryId() {
        return personDiaryId;
    }

    public void setPersonDiaryId(String personDiaryId) {
        this.personDiaryId = personDiaryId;
    }

    public String getRejectContent() {
        return rejectContent;
    }

    public void setRejectContent(String rejectContent) {
        this.rejectContent = rejectContent;
    }

    public boolean isApproval() {
        return isApproval;
    }

    public void setApproval(boolean approval) {
        isApproval = approval;
    }
}
