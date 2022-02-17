package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.donglaistd.jinli.Constant.UploadHandlerType.Upload_DEFAULT;

@Component
public class UploadVerifyUtil {
    @Autowired
    VerifyUtil verifyUtil;

    @Autowired
    DataManager dataManager;

    @Value("${data.avatar.save_path}")
    private String liveUserImgPath;

    @Value("${data.live-room.cover.save_path}")
    private String roomImgPath;

    public boolean verifyByHandler(UploadFileInfo uploadInfo){
        if (Strings.isBlank(uploadInfo.getHandlerType()) || uploadInfo.getFile_content_type().size() <= 0 || uploadInfo.getFile_path().size() <= 0)
            return false;
        Constant.UploadHandlerType handlerType = Constant.UploadHandlerType.forNumber(Integer.parseInt(uploadInfo.getHandlerType()));
        if(handlerType == null || handlerType.equals(Upload_DEFAULT)) return false;
        switch (handlerType){
            case DIARY:
            case REPORT:
            case ZONEIMAGE:
            case AVATAR:
            case LIVEUSERIMAGE:
            case UPDATEROOMIMAGE:
            case MUSIC:
                return verifyUtil.authUpload(uploadInfo);
            default:
                return verifyUploadBackGroundImage();
        }
    }

    private boolean verifyUploadBackGroundImage(){
        return true;
    }

    public File createFileIfNotExit(String filePath){
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public String getFilePathByUploadImageType(int type){
        switch (type){
            case 1:
                return liveUserImgPath;
            case 2:
                return roomImgPath;
            default:
                return "";
        }
    }
}
