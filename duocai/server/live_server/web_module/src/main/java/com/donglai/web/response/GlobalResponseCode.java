package com.donglai.web.response;

public enum GlobalResponseCode implements IErrorCode {
    SUCCESS(200, "操作成功！"),

    //账户相关
    NOT_AUTH_ERROR(401, "账户未登录"),
    PATH_NOT_EXIT(404,"页面不存在"),
    SERVER_ERROR(500,"服务器错误"),
    BAD_GATE_WAY(502,"错误的请求方式"),
    //权限
    ACCESS_FORBIDDEN_ERROR(403, "无访问权限"),
    USERNAME_OR_PASSWORD_ERROR(501, "您输入的用户名或密码不正确！"),
    ACCOUNT_LOCKED_ERROR(502, "账户被锁定！"),
    CREDENTIALS_EXPIRED_ERROR(503, "密码过期！"),
    ACCOUNT_EXPIRED_ERROR(504, "账户过期！"),
    ACCOUNT_DISABLED_ERROR(505, "账户被禁用！请联系管理员"),
    VALIDATE_CODE_NOT_MATCHED_ERROR(506, "验证码不匹配！"),
    LOGIN_FAILED_ERROR(507, "登录失败！"),
    ACCOUNT_EXISTS(508, "用户名已存在！"),

    USER_NOT_FOUND(509, "用户不存在"),
    PAGE_REQUEST_PARAM_ERROR(510, "分页查询参数错误"),
    PARAM_ERROR(511, "请求参数错误，请检查"),
    ROLE_NOT_EXISTS(512, "组别角色不存在"),
    ROLE_HAS_ADMIN(513, "不允许 修改/禁用/删除 拥有超级管理员的权限用户"),
    ROLE_IS_PLATFORM(514, "不允许 删除 平台方的用户账号"),
    ROLE_ILLEGALITY(515, "组别角色非法"),
    ROLE_NAME_EXISTS(516,"角色名重复"),

    MENU_NOT_EXISTS(600,"菜单不存在"),


    MISSING_PARAMETER(1005, "miss param"),
    NICK_NAME_USED_PARAMETER(1006, "nickname used"),
    TOKEN_EXPIRE(1007, "token expire!"),
    TOKEN_INVALID(1008, "token_invalid!"),
    ROOM_NOT_LIVE(1009, "房间未开播!"),

    PASSWORD_ILLEGALITY(1020,"密码非法"),
    UPLOAD_FILE_OVER_LIMIT(1050, "overFileLimit"),
    CDN_NOT_EXISTS(1051, "cdn 线路不存在"),
    CDN_DOMAIN_EMPTY(1051, "cdn 域名设置不能为空"),
    DOMAIN_NOT_EXISTS(1060, "域名不存在"),
    DOMAIN_REPETITION(1061, "域名重复"),
    DOMAIN_NUM_OVER_LIMIT(1062,"域名数量超过限制"),
    DOMAIN_ILLEGALITY(1063,"域名非法"),

    TOKEN_ERROR(2000, "auth-key 错误");


    private final int code;
    private final String message;

    GlobalResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
