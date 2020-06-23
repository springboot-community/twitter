package io.springboot.twitter.github;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import io.springboot.twitter.common.Message;
import io.springboot.twitter.exception.ServiceException;




@Component
public class GithubUploader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GithubUploader.class);
	
	public static final String URI_SEPARATOR = "/";
	
	public static final Set<String> ALLOW_FILE_SUFFIX = new HashSet<> (Arrays.asList("jpg", "png", "jpeg", "gif")); 
	
	@Value("${github.bucket.url}")
	private String url;
	
	@Value("${github.bucket.api}")
	private String api;
	
	@Value("${github.bucket.access-token}")
	private String accessToken;
	
	@Autowired
	RestTemplate restTemplate;
	
	public String upload (MultipartFile multipartFile, Long commiterId) throws IOException {
		
		String suffix = this.getSuffix(multipartFile.getOriginalFilename()).toLowerCase();
		if (!ALLOW_FILE_SUFFIX.contains(suffix)) {
			throw new ServiceException(Message.fail(Message.Code.BAD_REQUEST, "不支持的文件后缀：" + suffix));
		}
		
		// 重命名文件：用户ID-时间戳
		String fileName = commiterId.toString() + "-" + System.currentTimeMillis()  + "." + suffix;
		
		// 目录按照日期打散
		String[] folders = this.getDateFolder();
		
		// 最终的文件路径
		String filePath = new StringBuilder(String.join(URI_SEPARATOR, folders)).append(URI_SEPARATOR).append(fileName).toString();
		
		LOGGER.info("上传文件到Github：{}", filePath);
		
		JsonObject payload = new JsonObject();
		// 使用commit message，记录用户ID，上传涉政涉黄好查水表
		payload.add("message", new JsonPrimitive("uploaded by：" + commiterId.toString()));
		payload.add("content", new JsonPrimitive(Base64.getEncoder().encodeToString(multipartFile.getBytes())));
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		httpHeaders.set(HttpHeaders.AUTHORIZATION, "token " + this.accessToken);

		ResponseEntity<String> responseEntity = this.restTemplate.exchange(this.api + filePath, HttpMethod.PUT, 
				new HttpEntity<String>(payload.toString(), httpHeaders), String.class);
		
		if (responseEntity.getStatusCode().isError()) {
			throw new ServiceException(Message.fail(Message.Code.BAD_REQUEST, "文件提交失败：http status:" + responseEntity.getStatusCode().value()));
		}
		
		JsonObject response = JsonParser.parseString(responseEntity.getBody()).getAsJsonObject();
		
		LOGGER.info("上传完毕: {}", response.toString());
		
		
		// TODO 序列化到磁盘
		
		return this.url + filePath;
	}
	
	/**
	 * 获取文件的后缀
	 * @param fileName
	 * @return
	 */
	protected String getSuffix(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			String suffix = fileName.substring(index + 1);
			if (!suffix.isEmpty()) {
				return suffix;
			}
		}
		throw new ServiceException(Message.fail(Message.Code.BAD_REQUEST, "非法的文件名称：" + fileName));
	}

	/**
	 * 按照年月日获取打散的打散目录
	 * yyyy/mmd/dd
	 * @return
	 */
	protected String[] getDateFolder() {
		String[] retVal = new String[3];

		LocalDate localDate = LocalDate.now();
		retVal[0] = localDate.getYear() + "";

		int month = localDate.getMonthValue();
		retVal[1] = month < 10 ? "0" + month : month + "";

		int day = localDate.getDayOfMonth();
		retVal[2] = day < 10 ? "0" + day : day + "";

		return retVal;
	}
	
}
