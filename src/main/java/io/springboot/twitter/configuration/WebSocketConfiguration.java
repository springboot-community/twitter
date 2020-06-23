package io.springboot.twitter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import io.springboot.twitter.websocket.channel.TwitterChannel;

@Configuration  
public class WebSocketConfiguration {  
	
	@Bean  
	public ServerEndpointExporter serverEndpointExporter (){  
		ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();
		serverEndpointExporter.setAnnotatedEndpointClasses(TwitterChannel.class);
		return serverEndpointExporter;
	}  
}  