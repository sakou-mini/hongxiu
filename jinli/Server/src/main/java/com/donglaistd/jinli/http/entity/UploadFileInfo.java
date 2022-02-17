package com.donglaistd.jinli.http.entity;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadFileInfo {
    private List<String> file_content_type = new ArrayList<>();
    private List<String> file_path = new ArrayList<>();
    private String handlerType;
    private Integer uploadImageDescribe = 0;
    private String userId;
    private Map<String, String[]> examParam = new HashMap<>();
    private String remoteAddr;
    public List<String> getFile_content_type() {
        return file_content_type;
    }

    public void setFile_content_type(List<String> file_content_type) {
        this.file_content_type = file_content_type;
    }

    public List<String> getFile_path() {
        return file_path;
    }

    public void setFile_path(List<String> file_path) {
        this.file_path = file_path;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, String[]> getExamParam() {
        return examParam;
    }

    public String[] getExamParam(String key){
        return examParam.getOrDefault(key,new String[]{});
    }

    public Integer getUploadImageDescribe() {
        return uploadImageDescribe;
    }

    public void setUploadImageDescribe(Integer uploadImageDescribe) {
        this.uploadImageDescribe = uploadImageDescribe;
    }

    public void setExamParam(Map<String, String[]> examParam) {
        Map<String, String[]> tempParam = new HashMap<>();
        examParam.forEach((k,v)->{
            String[] values = new String[v.length];
            for (int i = 0; i < v.length; i++) {
                values[i] = URLDecoder.decode(v[i], StandardCharsets.UTF_8);
            }
            tempParam.put(k, values);
        });
        this.examParam = tempParam;
    }

    public boolean isNotImage(){
        return this.file_content_type.stream().anyMatch(type -> !type.startsWith("image/"));
    }

    public boolean isNotVideo(){
        return this.file_content_type.stream().anyMatch(type -> !type.startsWith("video/"));
    }
    public boolean isNotAudio(){
        return this.file_content_type.stream().anyMatch(type -> !type.startsWith("audio/"));
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String toString() {
        return "UploadFileInfo{" +
                "examParam=" + examParam +
                '}';
    }
}
