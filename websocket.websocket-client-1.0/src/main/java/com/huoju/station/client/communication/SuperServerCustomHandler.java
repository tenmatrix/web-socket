package com.huoju.station.client.communication;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class SuperServerCustomHandler extends
		SimpleChannelInboundHandler<WebSocketFrame> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg)
			throws Exception {
		if ((msg instanceof TextWebSocketFrame)) {
			// Send the uppercase string back.
			String request = ((TextWebSocketFrame) msg).text();
			System.err.printf("%s received %s%n", ctx.channel(), request);
			ctx.writeAndFlush(new TextWebSocketFrame(request.toUpperCase()));
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		
		if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
			
			ctx.channel().writeAndFlush(new TextWebSocketFrame("欢迎"));
			
			SuperServer.channelGroup.writeAndFlush(new TextWebSocketFrame(
					"Client " + ctx.channel() + " joined"));
			
			SuperServer.channelGroup.add(ctx.channel());
			
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
}
