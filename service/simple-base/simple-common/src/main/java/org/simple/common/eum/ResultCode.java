package org.simple.common.eum;


import org.simple.common.constant.IErrorCode;

public enum ResultCode implements IErrorCode {
    SUCCESS(0, "操作成功"),
    FAILED(1, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");
    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return message;
    }
}
