package io.springboot.twitter.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

import io.springboot.twitter.redis.ObjectRedisTemplate;

@Configuration
public class RedisConfiguration {
	
	@Bean
	public ObjectRedisTemplate objectRedisTemplate(@Autowired RedisConnectionFactory redisConnectionFactory) {
		ObjectRedisTemplate objectRedisTemplate = new ObjectRedisTemplate();
		objectRedisTemplate.setConnectionFactory(redisConnectionFactory);
		
		objectRedisTemplate.setKeySerializer(RedisSerializer.string());
		objectRedisTemplate.setValueSerializer(RedisSerializer.java());
		return objectRedisTemplate;
	}
}
