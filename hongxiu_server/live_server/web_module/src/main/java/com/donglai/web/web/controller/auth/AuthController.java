package com.donglai.web.web.controller.auth;

import com.donglai.common.service.RedisService;
import com.donglai.web.config.AuthRequestFactory;
import com.donglai.web.service.ThirdPartyAuth;
import com.donglai.web.web.dto.request.GetLoginInfoRequest;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.UuidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-10-25 14:49
 */

@RequestMapping("/api/v1/auth")
@RestController
@Slf4j
public class AuthController {
    @Autowired
    private AuthRequestFactory authRequestFactory;

    @Autowired
    private ThirdPartyAuth thirdPartyAuth;

    @Autowired
    private RedisService redisService;


    /**
     * 获取授权地址
     *
     * @param source   平台来源标识
     * @param response 跳转
     * @throws IOException 异常
     */
    @GetMapping("/login/{source}")
    public void login(@PathVariable String source, HttpServletResponse response) throws Exception {
        AuthRequest authRequest = authRequestFactory.getAuthRequest(source);
        if (Objects.isNull(authRequest)) {
            throw new Exception("没有找到所属平台!");
        }
        response.sendRedirect( authRequest.authorize(UuidUtils.getUUID()));
    }

    @RequestMapping("/callback/{source}")
    public void callback(@PathVariable("source") String source, AuthCallback callback, HttpServletResponse response) throws Exception {
        AuthRequest authRequest = authRequestFactory.getAuthRequest(source);
        int code = 200;
        if (Objects.isNull(authRequest)) {
            throw new Exception("没有找到具体实例");
        }
        AuthResponse login = authRequest.login(callback);
        code = login.getCode() != 2000 ? 500 : code;
        String state = "";
        if(code == 200){
            state = thirdPartyAuth.login(login.getData());
        }
        log.info("登录结果{}---->  服务器login {}",code, state);
        response.sendRedirect("http://52.128.228.82:8010/hongxiu/webResource/web-mobile/index.html?resultCode=" + code + state);
    }

    @PostMapping("/login/getInfo")
    public AuthResponse getUserLoginInfo(GetLoginInfoRequest getLoginInfoRequest) {
        return thirdPartyAuth.getUserLoginInfo(getLoginInfoRequest.getInfoId());
    }

    @PostMapping("/login/setUrl")
    public void setUrl(String key, String url) {
        log.info("key {} url {}", key, url);
        redisService.set(key, url);
    }

    @PostMapping("/login/getUrl")
    public void getUrl(String key, HttpServletResponse response) throws IOException {
        Object rul = redisService.get(key);
        if (Objects.nonNull(rul)) {
            response.sendRedirect(rul.toString());
        }
    }
    //@RequestMapping("/load/{path}")CC
    //public HttpServletResponse download(@PathVariable("path") String path,HttpServletResponse response) {
    //    try {
    //        // path是指欲下载的文件的路径。
    //        File file = new File(path);
    //        // 取得文件名。
    //        String filename = file.getName();
    //        // 取得文件的后缀名。
    //        String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
    //
    //        // 以流的形式下载文件。
    //        InputStream fis = new BufferedInputStream(new FileInputStream(path));
    //        byte[] buffer = new byte[fis.available()];
    //        fis.read(buffer);
    //        fis.close();
    //        // 清空response
    //        response.reset();
    //        // 设置response的Header
    //        response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
    //        response.addHeader("Content-Length", "" + file.length());
    //        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
    //        response.setContentType("application/octet-stream");
    //        toClient.write(buffer);
    //        toClient.flush();
    //        toClient.close();
    //    } catch (IOException ex) {
    //        ex.printStackTrace();
    //    }
    //    return response;
    //
    //}
}