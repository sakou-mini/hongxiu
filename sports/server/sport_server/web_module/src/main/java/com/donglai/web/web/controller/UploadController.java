package com.donglai.web.web.controller;

import com.donglai.model.dto.Pair;
import com.donglai.web.process.UploadProcess;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.GlobalResponseCode;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@Api(value = "UploadController", tags = "上传接口")
@RequestMapping("/api/v1")
@Slf4j
public class UploadController {
    /*@Value("${data.upload.file.depth}")
    int depth;

    @PostMapping("/uploadImage")
    @ResponseBody
    public Object uploadImage(HttpServletRequest request, HttpServletResponse response, UploadFileInfo uploadInfo) {
        log.info("info uploadImage is:{}", uploadInfo);
        if (uploadInfo.getFile_content_type().size() <= 0 || uploadInfo.isNotImage())
            return authError(response, "Image error");
        uploadInfo.setFile_path(parsePaths(uploadInfo.getFile_path()));
        return uploadInfo.getFile_path();
    }

    @PostMapping("/uploadVideo")
    @ResponseBody
    public Object uploadVideo(HttpServletResponse response, HttpServletRequest request, UploadFileInfo uploadInfo) {
        log.info("info uploadVideo is:{}", uploadInfo);
        if (uploadInfo.getFile_content_type().size() <= 0 || uploadInfo.isNotVideo())
            return authError(response, "Video error");
        uploadInfo.setFile_path(parsePaths(uploadInfo.getFile_path()));
        return uploadInfo.getFile_path();
    }

    @PostMapping("/uploadAudio")
    @ResponseBody
    public Object uploadAudio(HttpServletResponse response, HttpServletRequest request, UploadFileInfo uploadInfo) {
        log.info("info uploadAudio is:{}", uploadInfo);
        if (uploadInfo.getFile_content_type().size() <= 0 || uploadInfo.isNotAudio())
            return authError(response, "Audio error");
        uploadInfo.setFile_path(parsePaths(uploadInfo.getFile_path()));
        return uploadInfo.getFile_path();
    }

    private List<String> parsePaths(List<String> filePaths) {
        List<String> paths = new ArrayList<>();
        for (String filePath : filePaths) {
            String[] fs = filePath.split("[/]");
            StringBuilder path = new StringBuilder();
            for (int i = fs.length - 1, dep = 0; i >= 0 && dep < depth; i--, dep++) {
                path.insert(0, "/" + fs[i]);
            }
            paths.add(path.toString());
        }
        return paths;
    }

     private Object authError(HttpServletResponse response, String errorInfo) {
        response.setHeader("error", errorInfo);
        response.setStatus(500);
        return errorInfo;
    }
    */



    @Autowired
    UploadProcess uploadProcess;

    @PostMapping("/upload")
    public Object upload(@RequestParam(name = "file") List<MultipartFile> files){
        Pair<GlobalResponseCode, Object> uploadResult = uploadProcess.upload(files);
        if(!Objects.equals(uploadResult.getLeft(),GlobalResponseCode.SUCCESS))
            return new ErrorResponse(uploadResult.getLeft()).withData("uploadFiled");
        return uploadResult.getRight();
    }
}
