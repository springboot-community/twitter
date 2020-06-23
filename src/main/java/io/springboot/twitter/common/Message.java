package io.springboot.twitter.common;

import org.springframework.http.HttpStatus;



public class Message <T> {

    private Boolean success;

    private T data;

    private String message;
    
    private Integer code;
    
    private transient HttpStatus httpStatus;

    private Message(Boolean success, T data, Code code, HttpStatus httpStatus) {
        this(success, data, code.getCode(), code.getMessage(), httpStatus);
    }
    
    private Message(Boolean success, T data, Integer code, String message, HttpStatus httpStatus) {
        super();
        this.success = success;
        this.data = data;
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus == null ? HttpStatus.OK : httpStatus;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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

	public static <T> Message<T> success(T data){
        return new Message<>(Boolean.TRUE, data, Code.OK, null);
    }

    public static Message<Void> fail(Code code){
        return new Message<>(Boolean.FALSE, null, code, null);
    }
    
    public static Message<Void> fail(Code code, String message){
        return fail(code.getCode(), message, null);
    }

    private static Message<Void> fail(Integer code, String message, HttpStatus httpStatus){
        return new Message<>(Boolean.FALSE, null, code, message, httpStatus);
    }
    
    
    
    public static <T> Message<T> success(T data, HttpStatus httpStatus){
        return new Message<>(Boolean.TRUE, data, Code.OK, httpStatus);
    }

    public static Message<Void> fail(Code code, HttpStatus httpStatus){
        return new Message<>(Boolean.FALSE, null, code, httpStatus);
    }
    public static Message<Void> fail(Code code, String message, HttpStatus httpStatus){
        return fail(code.getCode(), message, httpStatus);
    }
    
//    public static Message<Void> fail(String message) {
//        return fail(null, message);
//    }
    
    
    public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}



	// 系统预定义状态
    public enum Code {
    	
    	OK(0, "ok"),
		
		NOT_FOUND(1, "资源不存在"),
		
		UNAUTHORIZED(2, "未登录"),
		
		FORBIDDEN(3, "无权操作"),
		
		BAD_REQUEST(4, "非法请求"),
		
		REQUIRED_VERIFY_CODE(5, "需要验证码"),
		
		// VERIFY_CODE_FAIL(6, "验证码错误"),
		
		INTERNAL_SERVER_ERROR(999, "服务器异常");
    	
    	
    	private final Integer code;
    	private final String message;
		private Code(Integer code, String message) {
			// this.code = this.ordinal();
			this.code = code;
			this.message = message;
		}
		public Integer getCode() {
			return code;
		}
		public String getMessage() {
			return message;
		}
    }
}
