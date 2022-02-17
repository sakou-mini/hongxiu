package com.donglai.web.process;

import com.donglai.common.util.StringUtils;
import com.donglai.model.dto.Pair;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.util.FastDFSUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.donglai.web.response.GlobalResponseCode.SUCCESS;
import static com.donglai.web.response.GlobalResponseCode.UPLOAD_FILE_OVER_LIMIT;

@Service
@Slf4j
public class UploadProcess {

    //图片文件类型
    public static String IMG_TYPE_PNG = "PNG";
    public static String IMG_TYPE_JPG = "JPG";
    public static String IMG_TYPE_JPEG = "JPEG";
    public static String IMG_TYPE_DMG = "BMP";
    public static String IMG_TYPE_GIF = "GIF";
    public static String IMG_TYPE_SVG = "SVG";
    public static final List<String> IMG_TYPES = Lists.newArrayList(IMG_TYPE_PNG, IMG_TYPE_JPG, IMG_TYPE_JPEG, IMG_TYPE_DMG, IMG_TYPE_GIF, IMG_TYPE_SVG);

    //视频文件类型
    public static String VIDEO_TYPE_MP4 = "MP4";
    public static String VIDEO_TYPE_3GPP = "3GPP";
    public static String VIDEO_TYPE_3GP = "3GP";
    public static String VIDEO_TYPE_MPEG = "MPEG";
    public static String VIDEO_TYPE_MPG = "MPG";
    public static String VIDEO_TYPE_MOV = "MOV";
    public static String VIDEO_TYPE_WEBM = "WEBM";
    public static String VIDEO_TYPE_SVG = "MNG";
    public static String VIDEO_TYPE_MNG = "AVI";
    public static String VIDEO_TYPE_WMV = "WMV";
    public static String VIDEO_TYPE_ASX = "ASX";
    public static String VIDEO_TYPE_TS = "TS";
    public static final List<String> VIDEO_TYPES = Lists.newArrayList(VIDEO_TYPE_MP4, VIDEO_TYPE_3GPP, VIDEO_TYPE_3GP, VIDEO_TYPE_MPEG,
            VIDEO_TYPE_MPG,VIDEO_TYPE_MOV,VIDEO_TYPE_WEBM,VIDEO_TYPE_SVG,VIDEO_TYPE_MNG,VIDEO_TYPE_WMV,VIDEO_TYPE_ASX,VIDEO_TYPE_TS);
    //音频类型
    public static final List<String> AUDIO_TYPES = Lists.newArrayList("MP3", "OGG", "M4A", "WMA");

    public long imageMaxSize = 51200;//50m
    public long videoMaxSize = 512000;//500m
    public long audioMaxSize = 51200;//50m

    public Pair<GlobalResponseCode,Object> upload(List<MultipartFile> files){
        List<String> uploadPath = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                long size_kb = file.getSize() /1024; //KB
                if(isImage(file) && size_kb > imageMaxSize || isVideo(file) && size_kb > videoMaxSize || isAudio(file) && size_kb > audioMaxSize) {
                    uploadPath.forEach(FastDFSUtil::deleteFile);
                    return new Pair<>(UPLOAD_FILE_OVER_LIMIT, null);
                }
                uploadPath.add(FastDFSUtil.uploadFile(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getCause().getMessage());
            return null;
        }
        return new Pair<>(SUCCESS,uploadPath);
    }



    public boolean isImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if(StringUtils.isNullOrBlank(originalFilename)) return false;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        return IMG_TYPES.contains(suffix.toUpperCase());
    }

    public boolean isVideo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if(StringUtils.isNullOrBlank(originalFilename)) return false;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        return VIDEO_TYPES.contains(suffix.toUpperCase());
    }

    public boolean isAudio(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if(StringUtils.isNullOrBlank(originalFilename)) return false;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        return AUDIO_TYPES.contains(suffix.toUpperCase());
    }

}
