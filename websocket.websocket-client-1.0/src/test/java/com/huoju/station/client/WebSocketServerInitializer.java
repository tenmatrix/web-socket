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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;

/**
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public WebSocketServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerProtocolHandler("/"));//处理websocket握手相关
        pipeline.addLast(new WebSocketServerCustomHandler());
//        pipeline.addLast(new WebSocketServerHandler());
        
//        Blocking operations 
//        While  the  I/O  thread must  not  be  blocked  at  all,  thus  prohibiting  any  direct  blocking  operations 
//        within  your  ChannelHandler,  there  is  a way  to  implement    this  requirement.  You  can  specify  an 
//        EventExecutorGroup  when  adding  ChannelHandlers  to  the  ChannelPipeline.  This 
//        EventExecutorGroup  will  then  be  used  to  obtain  an  EventExecutor,  which  will  execute  all  the 
//        methods  of  the  ChannelHandler.    This  EventExecutor  will  use  a  different  thread  from  the  I/O 
//        thread, thus freeing up the EventLoop. 
    }
}
