package io.springboot.twitter.constant;

public interface RedisKeys {

	String DELIMITER = ":";

	String USER = "user";
	
	String CONNECTION_TOKEN = "connection_token";
	
	String TWITTER_MESSAGE = "twitter_message";
	
	String USER_BANNED = "user_banned";
	
	public static String join (String ... keys) {
		return String.join(DELIMITER, keys);
	}
}
