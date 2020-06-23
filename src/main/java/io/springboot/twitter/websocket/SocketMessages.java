package io.springboot.twitter.websocket;

public interface SocketMessages {
	/**
	 * 禁言通知
	 */
	SocketMessage<String> BANNED_NOTIFY = new SocketMessage<>(SocketMessage.Code.NOTIFY, "你已经被禁言");
}
