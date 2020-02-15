package com.common.http;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum RespEnum {
    /*
    HTTP status code
     */
    OK(HttpStatus.OK),

    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    NOT_FOUND(HttpStatus.NOT_FOUND),

    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS),
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED),

    FILE_EMPTY(1000, "the file is empty"),

    REQUIRE_TOKEN(-100, "require access token firstly"),
    WRONG_APP_KEY_OR_SECRET(-102, "wrong app key or secret"),
    DUPLICATED_APP_KEY(-103, "duplicated app key"),
    FAIL_TO_ADD_APP_KEY(-104, "fail to add new app key"),
    FAIL_TO_ENABLE_APP_KEY(-105, "fail to enable app key"),

    REQUIRE_LOGIN(-200, "require login firstly"),
    WRONG_USER_NAME_OR_PWD(-202, "wrong user name or password"),
    DUPLICATED_USER_NAME(-204, "duplicated user name"),
    FAIL_TO_ADD_USER(-205, "fail to add new user"),
    FAIL_TO_ENABLE_USER(-206, "fail to enable user"),
    FAIL_TO_RESET_PWD(-207, "fail to reset password"),
    FAIL_TO_CHANGE_PWD(-208, "fail to change password"),
    WRONG_ORIGINAL_PWD(-209, "wrong original password"),
    UN_CHANGED_PWD(-210, "new password is same as original");

    RespEnum(HttpStatus status) {
        this.code = status.value();
        this.msg = status.getReasonPhrase();
    }

    RespEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Map<String, Object> toMap() {
        return new HashMap<String, Object>() {{
            put("code", code);
            put("msg", msg);
        }};
    }

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
