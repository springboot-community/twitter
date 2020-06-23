package io.springboot.twitter.domain.entity;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5621814389487194876L;
	// id
	private Long id;
	// 昵称
	private String name;
	// 账户
	private String url;
	// 头像
	private String avatar;
	// 是否被禁言
	private Boolean banned;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Boolean getBanned() {
		return banned;
	}
	public void setBanned(Boolean banned) {
		this.banned = banned;
	}
}
