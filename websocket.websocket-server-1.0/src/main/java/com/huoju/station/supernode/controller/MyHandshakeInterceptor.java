package com.huoju.station.supernode.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class MyHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
	private static final Logger logger = LoggerFactory
			.getLogger(MyHandshakeInterceptor.class);
	@Override
	public void afterHandshake(ServerHttpRequest paramServerHttpRequest,
			ServerHttpResponse paramServerHttpResponse,
			WebSocketHandler paramWebSocketHandler, Exception paramException) {
		logger.info("afterHandshake");
		super.afterHandshake(paramServerHttpRequest, paramServerHttpResponse, paramWebSocketHandler, paramException);
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest paramServerHttpRequest,
			ServerHttpResponse paramServerHttpResponse,
			WebSocketHandler paramWebSocketHandler, Map<String, Object> paramMap)
			throws Exception {
		logger.info("beforeHandshake");
		return super.beforeHandshake(paramServerHttpRequest, paramServerHttpResponse, paramWebSocketHandler, paramMap);
	}

}
