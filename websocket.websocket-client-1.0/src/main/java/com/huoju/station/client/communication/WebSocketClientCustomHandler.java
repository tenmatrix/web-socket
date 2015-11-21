package com.huoju.station.client.communication;

import com.huoju.station.client.WebSocketServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketClientCustomHandler extends
		SimpleChannelInboundHandler<Object> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println(msg instanceof WebSocketFrame );
		if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) msg;
            System.out.println("WebSocket Client received message: " + textFrame.text());
        }
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {System.out.println("balabala");
		if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
			System.out.println("完成握手");
//			// ctx.pipeline().remove(HttpRequestHandler.class);
//			ctx.channel().writeAndFlush(new TextWebSocketFrame("欢迎"));
//			WebSocketServer.channelGroup.writeAndFlush(new TextWebSocketFrame(
//					"Client " + ctx.channel() + " joined"));
//			WebSocketServer.channelGroup.add(ctx.channel());
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

}
