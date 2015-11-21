package com.huoju.station.client;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.CharsetUtil;

public class WebSocketServerCustomHandler extends
		SimpleChannelInboundHandler<Object> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, (FullHttpRequest) msg);// 处理web类型的消息，如html，css，image，js等
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);// 处理用户自定义的
		} 
	}

	private void handleHttpRequest(ChannelHandlerContext ctx,
			FullHttpRequest req) {
		System.out.println("http请求");
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		System.out.println("websocket请求");
		if ((frame instanceof TextWebSocketFrame)) {
			// throw new
			// UnsupportedOperationException(String.format("%s frame types not supported",
			// frame.getClass()
			// .getName()));

			// Send the uppercase string back.
			String request = ((TextWebSocketFrame) frame).text();
			System.err.printf("%s received %s%n", ctx.channel(), request);
			ctx.write(new TextWebSocketFrame(request.toUpperCase()));
		}
	}

	// private static void sendHttpResponse(ChannelHandlerContext ctx,
	// FullHttpRequest req, FullHttpResponse res) {
	// // Generate an error page if response getStatus code is not OK (200).
	// if (res.getStatus().code() != 200) {
	// ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),
	// CharsetUtil.UTF_8);
	// res.content().writeBytes(buf);
	// buf.release();
	// HttpHeaders.setContentLength(res, res.content().readableBytes());
	// }
	//
	// // Send the response and close the connection if necessary.
	// ChannelFuture f = ctx.channel().writeAndFlush(res);
	// if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
	// f.addListener(ChannelFutureListener.CLOSE);
	// }
	// }

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
			// ctx.pipeline().remove(HttpRequestHandler.class);
			ctx.channel().writeAndFlush(new TextWebSocketFrame("欢迎"));
			WebSocketServer.channelGroup.writeAndFlush(new TextWebSocketFrame(
					"Client " + ctx.channel() + " joined"));
			WebSocketServer.channelGroup.add(ctx.channel());
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

}
