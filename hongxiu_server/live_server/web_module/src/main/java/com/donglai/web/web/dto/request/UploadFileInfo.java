package com.donglai.web.web.dto.request;

import lombok.Data;
import lombok.ToString;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString
public class UploadFileInfo {
    private List<String> file_content_type = new ArrayList<>();
    private List<String> file_path = new ArrayList<>();
    private Integer uploadImageDescribe = 0;
    private String userId;
    private Map<String, String[]> examParam = new HashMap<>();
    private String remoteAddr;

    public void setExamParam(Map<String, String[]> examParam) {
        Map<String, String[]> tempParam = new HashMap<>();
        examParam.forEach((k, v) -> {
            String[] values = new String[v.length];
            for (int i = 0; i < v.length; i++) {
                values[i] = URLDecoder.decode(v[i], StandardCharsets.UTF_8);
            }
            tempParam.put(k, values);
        });
        this.examParam = tempParam;
    }

    public boolean isNotImage() {
        return this.file_content_type.stream().anyMatch(type -> !type.startsWith("image/"));
    }

    public boolean isNotVideo() {
        return this.file_content_type.stream().anyMatch(type -> !type.startsWith("video/"));
    }

    public boolean isNotAudio() {
        return this.file_content_type.stream().anyMatch(type -> !type.startsWith("audio/"));
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }
}
