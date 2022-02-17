package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import org.springframework.stereotype.Component;

@Component
public class GuessCoverUploadService extends RequestUploadService{

    @Override
    public Constant.ResultCode handle(UploadFileInfo uploadFileInfo, User user) {
        return Constant.ResultCode.SUCCESS;
    }
}
