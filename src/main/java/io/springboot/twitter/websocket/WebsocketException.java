package io.springboot.twitter.websocket;

import javax.websocket.CloseReason;

public class WebsocketException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5076655966424800726L;
	
	private CloseReason closeReason;

	public WebsocketException(CloseReason closeReason) {
		super(closeReason.getReasonPhrase());
		this.closeReason = closeReason;
	}
	
	public CloseReason closeReason () {
		return this.closeReason;
	}
}
