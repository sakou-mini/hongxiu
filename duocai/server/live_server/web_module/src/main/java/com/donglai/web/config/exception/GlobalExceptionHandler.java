package com.donglai.web.config.exception;

import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static com.donglai.common.util.GlobalExceptionUtil.getExceptionInfo;
import static com.donglai.web.response.GlobalResponseCode.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 系统异常处理，比如：404,500
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RestResponse defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        log.error("异常信息:{}", getExceptionInfo(e));
        RestResponse restResponse;
        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            restResponse = new ErrorResponse(PATH_NOT_EXIT);
        }else if(e instanceof HttpRequestMethodNotSupportedException){
            restResponse = new ErrorResponse(BAD_GATE_WAY);
        }
        else {
            restResponse = new ErrorResponse(SERVER_ERROR);
        }
        return restResponse;
    }

}
