package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.ReportDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.accusation.Report;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import com.donglaistd.jinli.util.DataManager;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.constant.WebKeyConstant.REPORTID;

@Service
public class ReportUploadService extends RequestUploadService{
    private static final Logger logger = Logger.getLogger(ReportUploadService.class.getName());

    @Autowired
    private ReportDaoService reportDaoService;
    @Autowired
    DataManager dataManager;
    @Value("${data.report.image.max}")
    private int MAX_NUMBER;
    @Override
    public Constant.ResultCode handle(UploadFileInfo uploadFileInfo,User user) {
        String reportId = uploadFileInfo.getExamParam(REPORTID)[0];
        if(Strings.isBlank(reportId)) {
            logger.info("reportId is null");
            return PARAM_ERROR;
        }
        Report report = reportDaoService.findById(reportId);
        if(Objects.isNull(report)) return REPORT_NOTFOUND;
        if (!Objects.equals(user.getId(),report.getInformantId())) return ILLEGAL_OPERATION;
        if((report.getImageUrlList().size() + uploadFileInfo.getFile_path().size()) > MAX_NUMBER) {
            logger.info("upload is overLimit");
            return UPLOADPARAM_OVERLIMIT;
        }
        report.addAllImageUrl(uploadFileInfo.getFile_path());
        reportDaoService.save(report);
        return SUCCESS;
    }
}
