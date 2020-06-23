package io.springboot.twitter.web.controller;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.springboot.twitter.constant.RedisKeys;
import io.springboot.twitter.web.SessionHolder;

@RequestMapping
@Controller
public class IndexController {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@GetMapping(value = { "/index", "" })
	public Object index() {
		
		Long userId = SessionHolder.USER_ID.get();
		
		String connectionToken = UUID.randomUUID().toString().replace("-", "");
		this.stringRedisTemplate.opsForValue().set(RedisKeys.join(RedisKeys.CONNECTION_TOKEN, userId.toString()),
				connectionToken, 5, TimeUnit.MINUTES);

		ModelAndView modelAndView = new ModelAndView("index/index");
		modelAndView.addObject("userId", userId);
		modelAndView.addObject("token", connectionToken);
		return modelAndView;
	}
}
