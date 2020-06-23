package io.springboot.twitter.web;

public class SessionHolder {
	public static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
}
