package com.huoju.station.supernode.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(WebSocketHandler.class);

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		logger.debug("afterConnectionClosed");
		System.out.println("afterConnectionClosed");
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		super.afterConnectionEstablished(session);
		logger.debug("afterConnectionEstablished");
		System.out.println("afterConnectionEstablished");
	}


	@Override
	protected void handlePongMessage(WebSocketSession session,
			PongMessage message) throws Exception {
		super.handlePongMessage(session, message);
		logger.debug("handlePongMessage");
		System.out.println("handlePongMessage");
	}

	@Override
	protected void handleTextMessage(WebSocketSession session,
			TextMessage message) throws Exception {
		super.handleTextMessage(session, message);
		logger.debug("handleTextMessage：{}",message.getPayload());
		System.out.println("handleTextMessage");
		session.sendMessage(new TextMessage("echo:"+message.getPayload()));
		
	}

	@Override
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		super.handleTransportError(session, exception);
		logger.debug("handleTransportError");
		System.out.println("handleTransportError");
	}

	@Override
	public boolean supportsPartialMessages() {
		logger.debug("supportsPartialMessages");
		System.out.println("supportsPartialMessages");
		return super.supportsPartialMessages();
	}

}
