package io.springboot.twitter.websocket.encoder;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import io.springboot.twitter.TwitterApplication;

public class JsonEncoder implements Encoder.Text<Object> {
	
	private Gson gson;

	@Override
	public void destroy() {
		
	}

	@Override
	public void init(EndpointConfig arg0) {
		this.gson = TwitterApplication.applicationContext.getBean(Gson.class);
	}

	@Override
	public String encode(Object arg0) throws EncodeException {
		return this.gson.toJson(arg0);
	}
}
