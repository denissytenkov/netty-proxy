package org.proxy.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.concurrent.ScheduledExecutorService;

public class NettyHttpServerHandler extends ChannelInitializer<SocketChannel> {
    private final ScheduledExecutorService executorService;

    public NettyHttpServerHandler(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new ServerRequestHandler(executorService));
    }
}
