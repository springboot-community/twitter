package io.springboot.twitter.web.controller;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.springboot.twitter.common.Message;
import io.springboot.twitter.github.GithubUploader;
import io.springboot.twitter.web.SessionHolder;

@RestController
@RequestMapping("/upload")
public class UploadController {
	
	@Autowired
	private GithubUploader githubUploader;
	
	@PostMapping
	public Object upload (@RequestParam("file") MultipartFile multipartFile) throws IOException {
		
		try(InputStream inputStream = multipartFile.getInputStream()){
			return Message.success(this.githubUploader.upload(multipartFile, SessionHolder.USER_ID.get()));
		}
	}
}
