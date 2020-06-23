package io.springboot.twitter.web.interceptor;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.WebUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;

import io.springboot.twitter.common.Messages;
import io.springboot.twitter.constant.CookieKeys;
import io.springboot.twitter.constant.JwtClaimKeys;
import io.springboot.twitter.web.SessionHolder;
/**
 * 
 * @author Administrator
 *
 */
// @SuppressWarnings("unused")
public class UserTokenInterceptor extends BaseHandlerInterceptor {

	static final Logger LOGGER = LoggerFactory.getLogger(UserTokenInterceptor.class);
	
	@Value("${jwt.token}")
	private String jwtToken;

	@Value("${oauth2.github.client-id}")
	private String clientId;
	
	
	@Autowired
	private Gson gson;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
		Cookie cookie = WebUtils.getCookie(request, CookieKeys.TOKEN);
		if (cookie != null) {
			try {
				DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(this.jwtToken)).build().verify(cookie.getValue());
				Long userId = decodedJWT.getClaim(JwtClaimKeys.USER_ID).asLong();
				SessionHolder.USER_ID.set(userId);
				return true;
			} catch (Exception e) {
				LOGGER.error("bad token: token={}, exception={}", cookie.getValue(), e.getMessage());
			}
		}
//		SessionHolder.USER_ID.set(1L);
//		return true;
		
		MediaType mediaType = io.springboot.twitter.utils.WebUtils.acceptMediaType(request);
		
		if (mediaType.equalsTypeAndSubtype(MediaType.TEXT_HTML)) {
			String state = UUID.randomUUID().toString().replace("-", "");
			// TODO 存储state用以校验
			response.sendRedirect("https://github.com/login/oauth/authorize?client_id=" + clientId + "&state=" + state);
		} else if (mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(this.gson.toJson(Messages.UNAUTHORIZED));
		} else {
			response.setContentType(MediaType.TEXT_PLAIN_VALUE);
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(Messages.UNAUTHORIZED.getMessage());
		}
		return false;
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		SessionHolder.USER_ID.remove();
	}
}
