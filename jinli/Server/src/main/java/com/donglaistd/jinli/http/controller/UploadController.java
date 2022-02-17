package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import com.donglaistd.jinli.http.service.dispacher.UploadDispatcher;
import com.donglaistd.jinli.util.UploadVerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("/upload")
public class UploadController {
    private static final Logger logger = Logger.getLogger(UploadController.class.getName());
    private static final List<String> CONTENT_TYPE = Arrays.asList("image/gif", "image/jpeg","image/png");
    private static final String OFFICIAL_UPLOAD_DEFAULT_PATH = "official";

    @Value("${data.video.file.depth}")
    int depth;
    @Value("${data.video.file.prefix}")
    String prefix;
    @Autowired
    UploadVerifyUtil uploadVerifyUtil;

    @Autowired
    UploadDispatcher uploadDispatcher;

    @RequestMapping("/uploadImage")
    @ResponseBody
    public Object uploadImage(HttpServletRequest request, HttpServletResponse response, UploadFileInfo uploadInfo){
        Map<String, String[]> parameterMap = request.getParameterMap();
        uploadInfo.setExamParam(parameterMap);
        uploadInfo.setRemoteAddr(request.getRemoteAddr());
        request.getParameterMap().forEach((key, value) -> logger.info("k " + key + " v = " + String.join(",", value)));
        if(!uploadVerifyUtil.verifyByHandler(uploadInfo) || uploadInfo.isNotImage())  {
            logger.info("UPLOAD__IMAGE:"+uploadInfo.getFile_path()+"FAILED！---->" +"VERIFY FAILED!");
            return authError(response, "error");
        }
        uploadInfo.setFile_path(parsePaths(uploadInfo.getFile_path()));
        Constant.ResultCode resultCode = uploadDispatcher.dispatcher(uploadInfo);
        if(!Objects.equals(Constant.ResultCode.SUCCESS,resultCode)){
            logger.info("UPLOAD__IMAGE:"+uploadInfo.getFile_path()+"FAILED！---->" +resultCode);
            return authError(response,resultCode.toString());
        }
        logger.info("UPLOAD__IMAGE:"+uploadInfo.getFile_path()+"SUCCESS！");
        return uploadInfo.getFile_path();
    }

    @RequestMapping("/uploadVideo")
    @ResponseBody
    public Object uploadVideo(HttpServletRequest request, HttpServletResponse response, UploadFileInfo uploadInfo){
        Map<String, String[]> parameterMap = request.getParameterMap();
        uploadInfo.setExamParam(parameterMap);
        request.getParameterMap().forEach((key, value) -> logger.info("k " + key + " v = " + String.join(",", value)));
        if(!uploadVerifyUtil.verifyByHandler(uploadInfo) || uploadInfo.isNotVideo()) {
            return authError(response, "error");
        }
        uploadInfo.setFile_path(parsePaths(uploadInfo.getFile_path()));
        Constant.ResultCode resultCode = uploadDispatcher.dispatcher(uploadInfo);
        if(!Objects.equals(Constant.ResultCode.SUCCESS,resultCode)) {
            return authError(response,resultCode.toString());
        }
        logger.info("UPLOAD__VIDEO:"+uploadInfo.getFile_path()+"SUCCESS！");
        return uploadInfo.getFile_path();
    }

    @RequestMapping("/uploadAudio")
    @ResponseBody
    public Object uploadAudio(HttpServletRequest request, HttpServletResponse response, UploadFileInfo uploadInfo){
        Map<String, String[]> parameterMap = request.getParameterMap();
        uploadInfo.setExamParam(parameterMap);
        uploadInfo.getExamParam().forEach((key, value) -> logger.info("k " + key + " v = " + String.join(",", value)));
        uploadInfo.setFile_path(parsePaths(uploadInfo.getFile_path()));
        if(uploadInfo.isNotAudio() || !uploadVerifyUtil.verifyByHandler(uploadInfo)) {
            return authError(response, "error");
        }
        Constant.ResultCode resultCode = uploadDispatcher.dispatcher(uploadInfo);
        if(!Objects.equals(Constant.ResultCode.SUCCESS,resultCode)) {
            return authError(response,resultCode.toString());
        }
        logger.info("UPLOAD__AUDIO:"+uploadInfo.getFile_path()+"SUCCESS！");
        return uploadInfo.getFile_path();
    }

    private List<String> parsePaths(List<String> filePaths){
        List<String> paths = new ArrayList<>();
        for (String filePath : filePaths) {
            String[] fs = filePath.split("[/]");
            StringBuilder path = new StringBuilder();
            for (int i = fs.length - 1, dep = 0; i >= 0 && dep < depth; i--, dep++) {
                path.insert(0,"/"+fs[i]);
            }
            paths.add(path.toString());
        }
        return paths;
    }

    private Object authError(HttpServletResponse response,String errorInfo) {
        logger.info("UPLOAD FAILED！");
        response.setHeader("error", errorInfo);
        response.setStatus(500);
        return errorInfo;
    }

    @RequestMapping("/uploadToServer")
    @ResponseBody
    public String uploadImageToServerLocal(@RequestParam("file") MultipartFile file , int imageType){
        String contentType = file.getContentType();
        String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(Calendar.getInstance().getTime());
        String filePath = uploadVerifyUtil.getFilePathByUploadImageType(imageType);
        if (filePath.isEmpty() || !CONTENT_TYPE.contains(contentType)) {
            logger.info("error contentType or filePath!");
            return "file type error! or imageType not contain!";
        }
        try {
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null) {
                logger.info("The contents of the file are illegal");
                return "The contents of the file are illegal";
            }
            var imagePath = uploadVerifyUtil.createFileIfNotExit(filePath + "/" + OFFICIAL_UPLOAD_DEFAULT_PATH);
            File newFile = new File(imagePath.getAbsolutePath()+"/" + fileName + "." + "png");
            file.transferTo(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OFFICIAL_UPLOAD_DEFAULT_PATH+"/"+fileName;
    }
}
