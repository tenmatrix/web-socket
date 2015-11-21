/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.huoju.station.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * Handles handshakes and messages
 * 使用SimpleChannelInboundHandler<Object>的Object泛型的意思是，不指定要处理那种特定的协议。
 * 如果用FullHttpRequest则说明只处理http协议；如果用WebSocketFrame，说明只用websocket协议
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
	private static final Logger logger = LoggerFactory.getLogger(WebSocketServerProtocolHandler.class);
    private static final String WEBSOCKET_PATH = "/websocket";

    private WebSocketServerHandshaker handshaker;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);//处理web类型的消息，如html，css，image，js等
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);//处理用户自定义的
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
    	System.out.println("http请求");
        // Handle a bad request.
        if (!req.getDecoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

//        // Allow only GET methods.
//        if (req.getMethod() != GET) {
//            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
//            return;
//        }

//        // Send the demo page and favicon.ico
//        if ("/".equals(req.getUri())) {
//            ByteBuf content = WebSocketServerIndexPage.getContent(getWebSocketLocation(req));
//            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
//
//            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
//            HttpHeaders.setContentLength(res, content.readableBytes());
//
//            sendHttpResponse(ctx, req, res);
//            return;
//        }
//        if ("/favicon.ico".equals(req.getUri())) {
//            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
//            sendHttpResponse(ctx, req, res);
//            return;
//        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
//            .addListener(new ChannelFutureListener(){
//
//				@Override
//				public void operationComplete(ChannelFuture future)
//						throws Exception {
//					logger.info("完成websocket握手");
//					future.channel().writeAndFlush(new TextWebSocketFrame("欢迎"));
//				}});
        }
    }

    @Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {System.out.println("触发用户事件"+evt.toString());
		if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
//		if (evt instanceof SslHandshakeCompletionEvent) {
			if(((SslHandshakeCompletionEvent)evt).isSuccess()){
				logger.info("完成触发用户事件websocket握手");
//				logger.info("{}",ctx.channel().isWritable());//.writeAndFlush("欢迎");
//				ctx.channel().writeAndFlush(new TextWebSocketFrame("欢迎"));
				WebSocketServer.channelGroup.add(ctx.channel());
				WebSocketServer.channelGroup.writeAndFlush(new TextWebSocketFrame(
						"Client " + ctx.channel() + " joined"));
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
    	System.out.println("websocket请求");
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
                    .getName()));
        }

        // Send the uppercase string back.
        String request = ((TextWebSocketFrame) frame).text();
        System.err.printf("%s received %s%n", ctx.channel(), request);
        ctx.write(new TextWebSocketFrame(request.toUpperCase()));
    }

    private static void sendHttpResponse(
            ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location =  req.headers().get(HOST) + WEBSOCKET_PATH;
        if (WebSocketServer.SSL) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }
}
