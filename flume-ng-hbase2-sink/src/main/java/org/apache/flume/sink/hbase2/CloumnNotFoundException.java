package org.apache.flume.sink.hbase2;

/**
 * 创建没有找到列名异常
 */
public class CloumnNotFoundException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -6463691496090059912L;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误消息
     */
    private String message;

    public CloumnNotFoundException(){}

    public CloumnNotFoundException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public CloumnNotFoundException(Throwable cause) {
        super(cause);
    }

    public CloumnNotFoundException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
