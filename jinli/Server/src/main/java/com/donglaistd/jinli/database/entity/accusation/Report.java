package com.donglaistd.jinli.database.entity.accusation;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Document
public class Report {
    @Id
    private ObjectId id= ObjectId.get();
    @Field
    private String informantId;
    @Field
    private String informantNickName;
    @Field
    private String contact;
    @Field
    private String reportedId;
    @Field
    private String reportedRoomId;
    @Field
    private String reportedNickName;

    @Field
    private Constant.ViolationType violationType;
    @Field
    private Date createDate;
    @Field
    private String content;
    @Field
    private List<String> imageUrlList = new ArrayList<>(3);
    @Field
    private boolean isHandle = false;

    public boolean addImageUrl(String path){
        return imageUrlList.add(path);
    }

    public Report(String whistleblowerId, String whistleblowerNickName, String contact,
                  String reportedId, String reportedRoomId, String reportedNickName, Constant.ViolationType violationType, Date createDate, String content) {
        this.informantId = whistleblowerId;
        this.informantNickName = whistleblowerNickName;
        this.contact = contact;
        this.reportedId = reportedId;
        this.reportedRoomId = reportedRoomId;
        this.reportedNickName = reportedNickName;
        this.violationType = violationType;
        this.createDate = createDate;
        this.content = content;
    }

    public static Report newInstance(String informantId, String informantNickName, String contact,
                                     String reportedId, String reportedRoomId, String reportedNickName, Constant.ViolationType violationType, String content) {
        return new Report(informantId, informantNickName, contact,reportedId, reportedRoomId, reportedNickName, violationType, new Date(), content);
    }

    public String getId() {
        return id.toString();
    }
    public boolean addAllImageUrl(List<String> paths){
        return imageUrlList.addAll(paths);
    }
    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", WhistleblowerId='" + informantId + '\'' +
                ", WhistleblowerNickName='" + informantNickName + '\'' +
                ", contact='" + contact + '\'' +
                ", reportedId='" + reportedId + '\'' +
                ", reportedRoomId='" + reportedRoomId + '\'' +
                ", reportedNickName='" + reportedNickName + '\'' +
                ", violationType=" + violationType +
                ", createDate=" + createDate +
                ", content='" + content + '\'' +
                ", imageUrlList=" + imageUrlList +
                '}';
    }

    public Report() {
    }

    public String getInformantId() {
        return informantId;
    }

    public void setInformantId(String informantId) {
        this.informantId = informantId;
    }

    public String getInformantNickName() {
        return informantNickName;
    }

    public void setInformantNickName(String informantNickName) {
        this.informantNickName = informantNickName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getReportedId() {
        return reportedId;
    }

    public void setReportedId(String reportedId) {
        this.reportedId = reportedId;
    }

    public String getReportedRoomId() {
        return reportedRoomId;
    }

    public void setReportedRoomId(String reportedRoomId) {
        this.reportedRoomId = reportedRoomId;
    }

    public String getReportedNickName() {
        return reportedNickName;
    }

    public void setReportedNickName(String reportedNickName) {
        this.reportedNickName = reportedNickName;
    }

    public Constant.ViolationType getViolationType() {
        return violationType;
    }

    public void setViolationType(Constant.ViolationType violationType) {
        this.violationType = violationType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public void setIsHandle(boolean isHandle) { this.isHandle = isHandle; }

    public boolean getIsHandle() { return isHandle ;}
}
