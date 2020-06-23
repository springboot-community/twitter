package io.springboot.twitter.websocket;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.springboot.twitter.domain.entity.User;
/**
 * 
 *  
 *
 */
public class TwitterMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -598037461271347444L;

	// 消息id
	private String id;
	// 用户
	private User user;
	// 消息发送时间
	private LocalDateTime dateTime;
	// 消息内容
	private String content;
	public TwitterMessage() {
		super();
	}
	public TwitterMessage(String id, User user, String content) {
		super();
		this.id = id;
		this.user = user;
		this.content = content;
	}
	public TwitterMessage(String id, User user, LocalDateTime dateTime, String content) {
		super();
		this.id = id;
		this.user = user;
		this.dateTime = dateTime;
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
}
