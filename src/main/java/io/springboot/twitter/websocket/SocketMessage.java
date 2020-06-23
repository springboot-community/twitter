package io.springboot.twitter.websocket;

import java.io.Serializable;

public class SocketMessage <T> implements Serializable {

	private static final long serialVersionUID = 7569315404416289947L;
	private Code code;
	private T data;
	public SocketMessage() {}
	public SocketMessage(Code code, T data) {
		super();
		this.code = code;
		this.data = data;
	}
	public Code getCode() {
		return code;
	}
	public void setCode(Code code) {
		this.code = code;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "SocketMessage [code=" + code + ", data=" + data + "]";
	}

	public static enum Code {

		// 系统的弹窗通知
		NOTIFY,
		
		// 用户被禁言通知
		BANNED,
		
		// 用户取消禁言通知
		CANCEL_BANNED,
		
		// 消息频率限制提醒
		MESSAGE_RATE_LIMIT,

		// 消息
		TWITTER_MESSAGE,
		
		// 新用户加入
		TWITTER_JOIN,
		
		// 在线用户退出
		TWITTER_QUIT,
		
		// 消息ACK
		TWITTER_MESSAGE_ACK,
		
		
		;
	}
}
