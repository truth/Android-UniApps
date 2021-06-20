package com.elevenzon.TextInputLayout;

/**
 * {
 *         "success": true,
 *         "message": "操作成功！",
 *         "code": 200,
 *         "result": [
 */
public class ApiResult {
    private boolean success;
    private String message;
    private Integer code;
    private Object result;
    private Long timestamp;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public Object getResult() {
        return result;
    }
    public void setResult(Object result) {
        this.result = result;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
