package org.proxy.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.asynchttpclient.AsyncHttpClient;

public class NettyHttpServerHandler extends ChannelInitializer<SocketChannel> {
    private final AsyncHttpClient asyncHttpClient;

    public NettyHttpServerHandler(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new ServerRequestHandler(asyncHttpClient));
    }
}
