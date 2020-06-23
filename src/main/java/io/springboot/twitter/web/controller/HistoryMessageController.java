package io.springboot.twitter.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.springboot.twitter.common.Message;
/**
 * 
 * TODO 历史消息
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/history")
public class HistoryMessageController {
	
	@GetMapping
	public Object twitterHistory () {
		return Message.success("");
	}
}
