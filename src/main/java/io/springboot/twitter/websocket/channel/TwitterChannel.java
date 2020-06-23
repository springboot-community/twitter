package io.springboot.twitter.websocket.channel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.springboot.twitter.TwitterApplication;
import io.springboot.twitter.constant.RedisKeys;
import io.springboot.twitter.domain.entity.User;
import io.springboot.twitter.utils.WebUtils;
import io.springboot.twitter.websocket.CloseReasons;
import io.springboot.twitter.websocket.ServerEndpointConfigurator;
import io.springboot.twitter.websocket.SocketMessage;
import io.springboot.twitter.websocket.SocketMessage.Code;
import io.springboot.twitter.websocket.SocketMessages;
import io.springboot.twitter.websocket.TwitterMessage;
import io.springboot.twitter.websocket.WebsocketException;
import io.springboot.twitter.websocket.decoder.TwitterMessageDecoder;
import io.springboot.twitter.websocket.encoder.JsonEncoder;

@ServerEndpoint(value = "/channel/twitter/{" + TwitterChannel.PATH_PARAM_NAME + "}", 
	configurator = ServerEndpointConfigurator.class, 
	encoders = { JsonEncoder.class },
	decoders = { TwitterMessageDecoder.class}
)
public class TwitterChannel {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterChannel.class);
	
	public static final String PATH_PARAM_NAME = "userId";
	
	public static final String TOKEN_PARAM_NAME = "token";
	
	private static final ConcurrentMap<String, TwitterChannel> CHANNELS = new ConcurrentHashMap<String, TwitterChannel>();
	
	private static final StringRedisTemplate stringRedisTemplate = TwitterApplication.applicationContext.getBean(StringRedisTemplate.class);
	
//	private static final Gson gson = TwitterApplication.applicationContext.getBean(Gson.class);
	
	private static final ThreadPoolTaskExecutor THREAD_POOL_TASK_EXECUTOR;
	
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	static {
		THREAD_POOL_TASK_EXECUTOR = new ThreadPoolTaskExecutor();
		THREAD_POOL_TASK_EXECUTOR.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2); 
		THREAD_POOL_TASK_EXECUTOR.setMaxPoolSize(20);
		THREAD_POOL_TASK_EXECUTOR.setQueueCapacity(100);
		THREAD_POOL_TASK_EXECUTOR.setDaemon(true);
		THREAD_POOL_TASK_EXECUTOR.setThreadNamePrefix("channel-task-");
		THREAD_POOL_TASK_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		
		THREAD_POOL_TASK_EXECUTOR.initialize();
	}
	
	/**
	 * 最大消息体积，20Kb
	 */
	public static final long MAX_MESSAGE_SIZE = 1024 * 20;
	
	/**
	 * 默认两次消息间隔，最低 1s
	 */
	public static final long DEFAULT_MESSAGE_INTERVAL = 1000;
	
	// 会话
	private Session session;
	
	// 用户对象
	private User user;
	
	// 链接时间
	private LocalDateTime connectedTime;
	
	// 最后一次消息时间
	private long lastSentTimestamp;
	
	private Predicate<TwitterChannel> notMe = null;
	
	public TwitterChannel () {	}

	@OnMessage(maxMessageSize = MAX_MESSAGE_SIZE)
	public void onMessage(SocketMessage<TwitterMessage> message) throws IOException {
		
		Code code = message.getCode();
		TwitterMessage twitterMessage = message.getData();
		if (code == null || code != SocketMessage.Code.TWITTER_MESSAGE) {
			this.close(CloseReasons.VIOLATED_POLICY);
			return ;
		}
		if (StringUtils.isEmpty(twitterMessage.getId())) {
			this.close(CloseReasons.VIOLATED_POLICY);
			return ;
		}
		if (StringUtils.isEmpty(twitterMessage.getContent())) {
			this.close(CloseReasons.VIOLATED_POLICY);
			return ;
		}
		
		/**
		 * TODO 内容安全由前端完成
		 */
		
		// 禁言判断
		if (this.user.getBanned()) {
			this.pushAsync(SocketMessages.BANNED_NOTIFY);
			return ;
		}
		
		// 消息频率限制
		long timestamp = System.currentTimeMillis();
		long tempTimestamp = this.lastSentTimestamp;
		
		this.lastSentTimestamp = timestamp;
		
		if ((timestamp - tempTimestamp) < DEFAULT_MESSAGE_INTERVAL) {
			this.pushAsync(new SocketMessage<>(SocketMessage.Code.MESSAGE_RATE_LIMIT, (DEFAULT_MESSAGE_INTERVAL / 1000)));
			return ;
		}
		
		LocalDateTime now = LocalDateTime.now();
		
		twitterMessage.setUser(this.user);
		twitterMessage.setDateTime(now);

		LOGGER.info("广播消息:user={}, content={}", this.user.getName(), message.getData().getContent());
		
		// 广播
		pushAsync(new SocketMessage<TwitterMessage>(SocketMessage.Code.TWITTER_MESSAGE, twitterMessage),  notMe)
			.whenCompleteAsync((result, exception) -> {
				if (exception != null) {
					// TODO 广播异常
				}
				this.pushAsync(new SocketMessage<String>(SocketMessage.Code.TWITTER_MESSAGE_ACK, twitterMessage.getId()));	
			}, THREAD_POOL_TASK_EXECUTOR)
			.whenCompleteAsync((result, exception) -> {
				//  TODO 消息持久化
			}, THREAD_POOL_TASK_EXECUTOR);
	}

	@OnOpen
	public void onOpen(Session session, @PathParam(PATH_PARAM_NAME) Long userId, EndpointConfig endpointConfig) throws IOException {
		
		this.session = session;
		this.connectedTime = LocalDateTime.now();
		
		LOGGER.info("新的连接：sessionId={},userId={},parameters={}", session.getId(), userId, session.getPathParameters());
		
		// TOKEN 校验
		List<String> tokenList = session.getRequestParameterMap().get(TOKEN_PARAM_NAME);
		if (tokenList == null || tokenList.size() != 1) {
			this.close(CloseReasons.BAD_TOEKN);
			return ;
		}
		String token = tokenList.get(0);
		if (StringUtils.isEmpty(token)) {
			this.close(CloseReasons.BAD_TOEKN);
			return ;
		}
		String tokenKey = RedisKeys.join(RedisKeys.CONNECTION_TOKEN, userId.toString());
		String connectionToken = stringRedisTemplate.opsForValue().get(tokenKey);
		if (StringUtils.isEmpty(connectionToken)) {
			this.close(CloseReasons.BAD_TOEKN);
			return ;
		}
		if (!connectionToken.equals(token)) {
			this.close(CloseReasons.BAD_TOEKN);
			return ;
		}
		THREAD_POOL_TASK_EXECUTOR.execute(() -> stringRedisTemplate.delete(tokenKey));
		
		// 用户身份信息
		String result = stringRedisTemplate.opsForValue().get(RedisKeys.join(RedisKeys.USER, userId.toString()));
		if (StringUtils.isEmpty(result)) {
			// TODO 用户身份不存在
			this.close(CloseReasons.BAD_TOEKN);
			return ;
		}
		JsonObject userJson = JsonParser.parseString(result).getAsJsonObject();
		this.user = new User();
		this.user.setId(userJson.get("id").getAsLong());
		this.user.setAvatar(userJson.get("avatar_url").getAsString());
		this.user.setName(userJson.get("login").getAsString());
		this.user.setUrl(userJson.get("html_url").getAsString());
		this.user.setBanned(isBanned(userId));
		
		this.session.setMaxIdleTimeout(-1);
		this.notMe = (channel) -> !channel.session.getId().equals(this.session.getId());
		
		
		// 尝试踢出重复登录的用户
		TwitterChannel existsChannel = CHANNELS.put(session.getId(), this);
		if (existsChannel != null) {
			existsChannel.close(CloseReasons.REPEATEDLY_CONNECTION);
		}
		
		// 广播join消息
		pushAsync(new SocketMessage<TwitterMessage>(Code.TWITTER_JOIN, new TwitterMessage(null, this.user, null)), this.notMe);
	}

	@OnClose
	public void onClose(CloseReason closeReason) {
		LOGGER.info("连接断开：id={},reason={}, 链接时间={}, 断开时间={}", this.session.getId(), closeReason,
				DATE_TIME_FORMATTER.format(this.connectedTime), DATE_TIME_FORMATTER.format(LocalDateTime.now()));
		if (closeReason == CloseReasons.BAD_TOEKN) {
			// 非法链接断开不需要广播
			return ;
		}
		
		TwitterChannel twitterChannel = CHANNELS.remove(this.session.getId());
		if (twitterChannel != null && twitterChannel.user != null) {
			pushAsync(new SocketMessage<>(Code.TWITTER_QUIT, new TwitterMessage(null, twitterChannel.user, null)), this.notMe);
		}
	}

	@OnError
	public void onError(Throwable throwable) throws IOException {
		
		LOGGER.info("连接异常：id={},throwable={}", this.session.getId(), throwable == null ? null : throwable.getMessage());
		
		CloseReason closeReason = null;
		
		if (throwable instanceof WebsocketException) {
			closeReason = ((WebsocketException) throwable).closeReason();
		} else if (throwable instanceof JsonSyntaxException || 
				throwable instanceof IllegalArgumentException) {
			closeReason = CloseReasons.VIOLATED_POLICY;
		} else {
			closeReason = new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "系统异常:" + throwable.getClass().getName());
		}
		
		LOGGER.error(WebUtils.throwableStackTrace(throwable));
		
		this.session.close(closeReason);
	}
	
	// methods ---------------------------
	public Future<Void> pushAsync (Object message) {
		return this.session.getAsyncRemote().sendObject(message);
	}
	
	public void close (CloseReason closeReason) throws IOException {
		this.session.close(closeReason);
	}
	
	// static methods -------------------------------------
	public static CompletableFuture<Void> pushAsync(SocketMessage<?> message, Predicate<TwitterChannel> predicate) {
		return CompletableFuture.runAsync(() -> {
			CHANNELS.values().parallelStream().forEach(channel -> {
				if (predicate != null && !predicate.test(channel)){
					return ;
				}
				if (channel.session.isOpen()) {
					channel.session.getAsyncRemote().sendObject(message);
				}
			});
		}, THREAD_POOL_TASK_EXECUTOR);
	}
	
	public static List<User> users (){
		return CHANNELS.values().stream()
				.sorted((t1, t2) -> t1.connectedTime.compareTo(t2.connectedTime))
				.map((t) -> t.user)
				.collect(Collectors.toList());
	}
	
	public static Boolean isBanned(Long userId) {
		HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
		String retVal = hashOperations.get(RedisKeys.USER_BANNED, userId.toString());
		return retVal == null ? Boolean.FALSE : new Boolean(retVal);
	}
}
