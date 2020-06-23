package io.springboot.twitter.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import io.springboot.twitter.common.Message;
import io.springboot.twitter.websocket.channel.TwitterChannel;

@RestController
@RequestMapping("/user")
public class UserController {
	
	/**
	 * 在线用户
	 * @return
	 */
	@GetMapping("/online")
	public Object onlineUsers () {
		return Message.success(TwitterChannel.users());
	}
}
