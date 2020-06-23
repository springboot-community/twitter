package io.springboot.twitter.constant;

import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 *
 */
public class SystemProperties {

	/**
	 * Token最长时间，7天
	 */
	public static final int MAX_TOKEN_EXPIRE = (int) TimeUnit.DAYS.toSeconds(7);
}
