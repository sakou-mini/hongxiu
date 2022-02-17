package com.donglai.web.response;

public enum GlobalResponseCode implements IErrorCode {
    SUCCESS(200, "操作成功！"),

    //账户相关
    NOT_AUTH_ERROR(401, "账户未登录"),
    //权限
    ACCESS_FORBIDDEN_ERROR(403, "无访问权限"),
    USERNAME_OR_PASSWORD_ERROR(501, "您输入的用户名或密码不正确！"),
    ACCOUNT_LOCKED_ERROR(502, "账户被锁定！"),
    CREDENTIALS_EXPIRED_ERROR(503, "密码过期！"),
    ACCOUNT_EXPIRED_ERROR(504, "账户过期！"),
    ACCOUNT_DISABLED_ERROR(505, "账户被禁用！"),
    VALIDATE_CODE_NOT_MATCHED_ERROR(506, "验证码不匹配！"),
    LOGIN_FAILED_ERROR(507, "登录失败！"),
    USERNAME_NOT_FOUND_ERROR(508, "用户名不存在！"),

    USER_NOT_FOUND(509, "用户不存在"),
    PAGE_REQUEST_PARAM_ERROR(510, "分页查询参数错误"),
    PARAM_ERROR(511, "请求参数错误，请检查"),
    LABEL_ERROR(512, "标签名重复"),
    //动态错误
    CREATE_BLOGS_FAILED(1000, "创建博客失败"),
    BLOGS_APPROVAL_STATUS_ERROR(1001, "博客审核状态有误！"),
    BLOGS_NOT_EXIST(1002, "博客不存在！"),
    //动态评论
    COMMENT_NOT_EXIT(1003, "评论不存在"),
    //动态音乐
    MUSIC_NOT_EXIT(1011, "音乐不存在"),
    MUSIC_PARAM_ERROR(1010, "音乐参数错误"),

    MISSING_PARAMETER(1005, "miss param"),
    NICK_NAME_USED_PARAMETER(1006, "nickname used"),
    TOKEN_EXPIRE(1007, "token expire!"),
    TOKEN_INVALID(1008, "token_invalid!"),
    ROOM_NOT_LIVE(1009, "房间未开播!"),
    UPLOAD_FILE_OVER_LIMIT(1050, "overFileLimit"),
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
