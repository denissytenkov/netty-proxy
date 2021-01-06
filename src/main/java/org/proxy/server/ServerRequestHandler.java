package org.proxy.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerRequestHandler extends SimpleChannelInboundHandler<Object> {
    private final ScheduledExecutorService executorService;

    protected ServerRequestHandler(ScheduledExecutorService executorService) {
        super();
        this.executorService = executorService;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        HttpRequest request = null;

        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
            handleRequest(ctx, request);
        }
    }

    private void handleRequest(ChannelHandlerContext ctx, HttpRequest request) {
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }
        executorService.schedule(() -> {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
            ctx.writeAndFlush(response);
            ctx.close();
        }, 5, TimeUnit.SECONDS);
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
