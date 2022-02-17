package com.donglaistd.jinli.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.logging.Logger;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {
    private static final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    public static String getExceptionInfo(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuffer sb = new StringBuffer();
        sb.append(e.toString()).append("\r\n");
        for (StackTraceElement stackTraceElement : stackTrace) {
            sb.append("\tat ").append(stackTraceElement.getClassName()).append(".")
                    .append(stackTraceElement.getMethodName())
                    .append("(").append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber())
                    .append(")\r\n");
        }
        return sb.toString();
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void globalExceptionHandle(Exception e) {
        log.warning("===========全局统一异常处理============");
        log.warning(getExceptionInfo(e));
    }
}
