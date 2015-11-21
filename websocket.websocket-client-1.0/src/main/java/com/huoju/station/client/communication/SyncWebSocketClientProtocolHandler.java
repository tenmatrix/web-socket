package com.huoju.station.client.communication;

import java.net.URI;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class SyncWebSocketClientProtocolHandler extends
		WebSocketClientProtocolHandler {
	private ChannelPromise handshakeFuture;
	public ChannelFuture handshakeFuture() {
		return handshakeFuture;
	}

	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		super.handlerAdded(ctx);
		handshakeFuture = ctx.newPromise();
	}

	public SyncWebSocketClientProtocolHandler(URI webSocketURL,
			WebSocketVersion version, String subprotocol,
			boolean allowExtensions, HttpHeaders customHeaders,
			int maxFramePayloadLength, boolean handleCloseFrames) {
		super(webSocketURL, version, subprotocol, allowExtensions,
				customHeaders, maxFramePayloadLength, handleCloseFrames);
	}

	public SyncWebSocketClientProtocolHandler(URI webSocketURL,
			WebSocketVersion version, String subprotocol,
			boolean allowExtensions, HttpHeaders customHeaders,
			int maxFramePayloadLength) {
		super(webSocketURL, version, subprotocol, allowExtensions,
				customHeaders, maxFramePayloadLength);
	}

	public SyncWebSocketClientProtocolHandler(
			WebSocketClientHandshaker handshaker, boolean handleCloseFrames) {
		super(handshaker, handleCloseFrames);
	}

	public SyncWebSocketClientProtocolHandler(
			WebSocketClientHandshaker handshaker) {
		super(handshaker);
	}

}
