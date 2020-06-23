package io.springboot.twitter.websocket.decoder;

import java.lang.reflect.Type;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.springboot.twitter.TwitterApplication;
import io.springboot.twitter.websocket.SocketMessage;
import io.springboot.twitter.websocket.TwitterMessage;
/*

Request Payload

{
	"code": "TWITTER_MESSAGE",
	"data": {
		"id": "423602daff0f44149b085df6e75a8159",
		"content": "Hello World!"
	}
}
*/
public class TwitterMessageDecoder implements Decoder.Text <SocketMessage<TwitterMessage>> {
	
	private Type messageType;

	private Gson gson;
	
	@Override
	public void destroy() {
	}

	@Override
	public void init(EndpointConfig arg0) {
		this.messageType = new TypeToken<SocketMessage<TwitterMessage>>() {}.getType();
		this.gson = TwitterApplication.applicationContext.getBean(Gson.class);
	}

	@Override
	public SocketMessage<TwitterMessage> decode(String arg0) throws DecodeException {
		return gson.fromJson(arg0, this.messageType);
	}

	@Override
	public boolean willDecode(String arg0) {
		return true;
	}
}
