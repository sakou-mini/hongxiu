package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.UploadFileInfo;

public abstract class RequestUploadService {
    public abstract Constant.ResultCode handle(UploadFileInfo uploadFileInfo, User user);
}
