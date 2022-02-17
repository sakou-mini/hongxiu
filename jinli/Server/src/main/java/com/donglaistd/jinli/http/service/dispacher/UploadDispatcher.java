package com.donglaistd.jinli.http.service.dispacher;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import com.donglaistd.jinli.http.service.RequestUploadService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.UNKNOWN;

@Component
public class UploadDispatcher implements ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(UploadDispatcher.class.getName());
    private final Map<String, RequestUploadService> beans = new ConcurrentHashMap<>();
    @Autowired
    DataManager dataManager;

    public Constant.ResultCode dispatcher(UploadFileInfo uploadFileInfo) {
        logger.fine("receive upload" + uploadFileInfo);
        String handlerType = Constant.UploadHandlerType.forNumber(Integer.parseInt(uploadFileInfo.getHandlerType())).toString();
        RequestUploadService uploadService = beans.get(handlerType);
        if (Objects.isNull(uploadService)) {
            logger.warning("no handler for message type:" + uploadFileInfo.getHandlerType());
            return UNKNOWN;
        }
        User user = null;
        if(!StringUtils.isNullOrBlank(uploadFileInfo.getUserId()))
            user = dataManager.findUser(uploadFileInfo.getUserId());
        return uploadService.handle(uploadFileInfo, user);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        var contextBeans = applicationContext.getBeansOfType(RequestUploadService.class);
        for (var bean : contextBeans.entrySet()) {
            beans.put(bean.getKey().toUpperCase().replace("UPLOADSERVICE", ""), bean.getValue());
        }
    }
}
