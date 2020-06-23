package io.springboot.twitter.websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import io.undertow.websockets.jsr.ServerEndpointConfigImpl;

public class ServerEndpointConfigurator extends ServerEndpointConfigImpl.Configurator {
	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
		// TODO 检查Origin
		super.modifyHandshake(sec, request, response);
	}
}