package io.springboot.twitter.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.springboot.twitter.common.Message;
import io.springboot.twitter.constant.CookieKeys;
import io.springboot.twitter.constant.JwtClaimKeys;
import io.springboot.twitter.constant.RedisKeys;
import io.springboot.twitter.exception.ServiceException;

@Controller
@RequestMapping("/oauth2")
public class Oauth2CallbackController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Oauth2CallbackController.class);

	private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";

	private static final String USER_URL = "https://api.github.com/user";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Value("${jwt.token}")
	private String jwtToken;
	
	@Value("${oauth2.github.client-id}")
	private String clientId;
	
	@Value("${oauth2.github.client-secret}")
	private String clientSecret;
	
	@GetMapping("/github/callback")
	public Object githubCallback (HttpServletRequest request,
							HttpServletResponse response,
							@RequestParam("code") String code,
							@RequestParam("state") String state) {
		
		LOGGER.info("Github Oath Callback:code={}, state={}", code, state);
		
		// TODO state 校验
		
		/**
		 * 使用code获取accessToken
		 */
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("client_id", this.clientId);
		requestBody.add("client_secret", this.clientSecret);
		requestBody.add("code", code);


		ResponseEntity<String> tokenResponseEntity = this.restTemplate.postForEntity(ACCESS_TOKEN_URL, new HttpEntity<>(requestBody, httpHeaders), String.class);
		
		LOGGER.info("Github Oath Callback: response={}", tokenResponseEntity);
		
		if (tokenResponseEntity.getStatusCode().is5xxServerError()) {
			throw new ServiceException(Message.fail(Message.Code.INTERNAL_SERVER_ERROR, "Github授权服务异常", HttpStatus.INTERNAL_SERVER_ERROR));
		}
		
		JsonObject jsonObject = JsonParser.parseString(tokenResponseEntity.getBody()).getAsJsonObject();
		
		if (jsonObject.has("error")) {
			throw new ServiceException(Message.fail(Message.Code.BAD_REQUEST, jsonObject.get("error_description").getAsString(), HttpStatus.BAD_REQUEST));
		}
		
		String accessToken = jsonObject.get("access_token").getAsString();
		String scope = jsonObject.get("scope").getAsString();
		String tokenType = jsonObject.get("token_type").getAsString();

		LOGGER.info("Github Oath Callback:accessToken={}, scope={}, tokenType={}", accessToken, scope, tokenType);

		/**
		 * 通过accessToken获取用户信息
		 */
		httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.AUTHORIZATION, "token " + accessToken);
		httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		ResponseEntity<String> userResponseEntity = this.restTemplate.exchange(USER_URL, HttpMethod.GET, new HttpEntity<Void>(null, httpHeaders), String.class);
		if (userResponseEntity.getStatusCode().is5xxServerError()) {
			throw new ServiceException(Message.fail(Message.Code.INTERNAL_SERVER_ERROR, "Github授权服务异常", HttpStatus.INTERNAL_SERVER_ERROR));
		}
		
		JsonObject userJson = JsonParser.parseString(userResponseEntity.getBody()).getAsJsonObject();
		
		if (userJson.has("error")) {
			throw new ServiceException(Message.fail(Message.Code.BAD_REQUEST, jsonObject.get("error_description").getAsString(), HttpStatus.BAD_REQUEST));
		}

		LOGGER.info("Github Oath Callback:user={}", userJson);
		
		Long userId = userJson.get("id").getAsLong();
		
		/**
		 * 持久化用户身份信息到Redis
		 */
		this.stringRedisTemplate.opsForValue().set(RedisKeys.join(RedisKeys.USER, userId.toString()), userJson.toString());

		/**
		 * token授权
		 */
//		LocalDateTime issuedAt = LocalDateTime.now();
//		LocalDateTime expiresAt = issuedAt.plusDays(7); 
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + (TimeUnit.DAYS.toMillis(7));  // token有效期，7天
		
		Map<String, Object> jwtHeader = new HashMap<>(2);
		jwtHeader.put("alg", "HS256");
		jwtHeader.put("typ", "JWT");
		String token = JWT.create()
				.withHeader(jwtHeader)
				.withClaim(JwtClaimKeys.USER_ID, userId)
				.withIssuedAt(new Date(issuedAt))
				.withExpiresAt(new Date(expiresAt))
				.sign(Algorithm.HMAC256(this.jwtToken));
		
		Cookie cookie = new Cookie(CookieKeys.TOKEN, token);
		cookie.setMaxAge((int) ((expiresAt - issuedAt) / 1000));
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
		
		return new ModelAndView("redirect:/");
	}
	
	@GetMapping("/springboot/callback")
	public Object springbootCallback () {
		// TODO 社区授权
		return new ModelAndView("redirect:/");
	}
}

