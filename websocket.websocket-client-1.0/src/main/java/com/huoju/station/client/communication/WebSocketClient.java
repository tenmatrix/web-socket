/*
 * Copyright 2014 The Netty Project
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
package com.huoju.station.client.communication;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * This is an example of a WebSocket client.
 * <p>
 * In order to run this example you need a compatible WebSocket server.
 * Therefore you can either start the WebSocket server from the examples by
 * running {@link io.netty.example.http.websocketx.server.WebSocketServer} or
 * connect to an existing WebSocket server such as <a
 * href="http://www.websocket.org/echo.html">ws://echo.websocket.org</a>.
 * <p>
 * The client will attempt to connect to the URI passed to it as the first
 * argument. You don't have to specify any arguments if you want to connect to
 * the example WebSocket server, as this is the default.
 */
public final class WebSocketClient {

	// static final String URL = System.getProperty("url",
	// "wss://127.0.0.1:8443/websocket");
//	static final String URL = System.getProperty("url",
//			"ws://127.0.0.1:8080/websocket");
	static final String URL = System.getProperty("url",
			"ws://127.0.0.1:7788/huoju-supernode/myHandler");
	private Channel ch; 
	public static void main(String[] args) throws Exception {
		WebSocketClient me = new WebSocketClient();
		me.start();
	}

	public void start() throws Exception {
		URI uri = new URI(URL);
		String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
		final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
		final int port;
		if (uri.getPort() == -1) {
			if ("http".equalsIgnoreCase(scheme)) {
				port = 80;
			} else if ("https".equalsIgnoreCase(scheme)) {
				port = 443;
			} else {
				port = -1;
			}
		} else {
			port = uri.getPort();
		}

		if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
			System.err.println("Only WS(S) is supported.");
			return;
		}

		final boolean ssl = "wss".equalsIgnoreCase(scheme);
		final SslContext sslCtx;
		if (ssl) {
			sslCtx = SslContext
					.newClientContext(InsecureTrustManagerFactory.INSTANCE);
		} else {
			sslCtx = null;
		}

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			// Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08
			// or V00.
			// If you change it to V00, ping is not supported and remember to
			// change
			// HttpResponseDecoder to WebSocketHttpResponseDecoder in the
			// pipeline.
			// final WebSocketClientHandler handler =
			// new WebSocketClientHandler(
//			WebSocketClientHandshaker newHandshaker = WebSocketClientHandshakerFactory
//					.newHandshaker(uri, WebSocketVersion.V13, null, false,
//							new DefaultHttpHeaders());
			// );
//			WebSocketClientInitializer initializer = new WebSocketClientInitializer(
//					sslCtx, host, port, newHandshaker);
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.handler(new WebSocketClientInitializer(sslCtx, host, port, uri));

			ch = b.connect(uri.getHost(), port).sync().channel();// 通道建立阻塞
			// newHandshaker.sync();//握手操作执行完成前阻塞
			System.out.println("等待握手");
//			ch.closeFuture().sync();
			
			BufferedReader console = new BufferedReader(new InputStreamReader(
					System.in, Charset.forName("UTF-8")));
			while (true) {
				String msg =console.readLine();
				if (msg == null) {
					break;
				} else if ("bye".equals(msg.toLowerCase())) {
					ch.writeAndFlush(new CloseWebSocketFrame());
					ch.closeFuture().sync();
					break;
				} else if ("ping".equals(msg.toLowerCase())) {
					WebSocketFrame frame = new PingWebSocketFrame(
							Unpooled.wrappedBuffer(new byte[] { 8, 1, 8, 1 }));
					ch.writeAndFlush(frame);
				} else {
					WebSocketFrame frame = new TextWebSocketFrame(new String(
							msg.getBytes(), Charset.forName("UTF-8")));
					System.out.println("发送消息:"+msg);
					ch.writeAndFlush(frame);
				}
			}
		} finally {
			group.shutdownGracefully();
		}
	}

	public void sendMsg(String msg){
		System.err.println("发送消息:"+msg);
		ch.writeAndFlush(new TextWebSocketFrame(msg));
	}
	
	class WebSocketClientInitializer extends ChannelInitializer<SocketChannel> {
		private SslContext sslCtx;
		private String host;
		private int port;
		private URI uri;
		private WebSocketClientHandshaker handShaker;
		private WebSocketClientProtocolHandler webSocketClientProtocolHandler;

		public WebSocketClientInitializer(SslContext sslCtx, String host,
				int port, URI uri) {
			this.sslCtx = sslCtx;
			this.host = host;
			this.port = port;
			this.uri = uri;
System.out.println("uri::"+uri);
			handShaker = WebSocketClientHandshakerFactory
					.newHandshaker(uri, WebSocketVersion.V13, null, false,
							new DefaultHttpHeaders());
			
			webSocketClientProtocolHandler = new WebSocketClientProtocolHandler(handShaker);

		}

		public WebSocketClientInitializer(SslContext sslCtx, String host,
				int port,/* URI uri, */WebSocketClientHandshaker handShaker) {
			this.sslCtx = sslCtx;
			this.host = host;
			this.port = port;
			// this.uri = uri;
			this.handShaker = handShaker;

			webSocketClientProtocolHandler = new SyncWebSocketClientProtocolHandler(
					handShaker);

		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline p = ch.pipeline();
			p.addFirst(new LoggingHandler(LogLevel.INFO));
			if (sslCtx != null) {
				p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
			}
			p.addLast(new HttpClientCodec(),
					new HttpObjectAggregator(8192),
					webSocketClientProtocolHandler,
					new WebSocketClientCustomHandler());
		}

//		public ChannelFuture getHandshakeFuture() {
//			return ((SyncWebSocketClientProtocolHandler) webSocketClientProtocolHandler)
//					.handshakeFuture();
//		}

	}
}
