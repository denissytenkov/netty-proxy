package org.proxy.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.request.body.generator.UnboundedQueueFeedableBodyGenerator;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerRequestHandler extends SimpleChannelInboundHandler<Object> {
    private final UnboundedQueueFeedableBodyGenerator bodyGenerator = new UnboundedQueueFeedableBodyGenerator();
    private final AsyncHttpClient asyncHttpClient;

    protected ServerRequestHandler(AsyncHttpClient asyncHttpClient) {
        super();
        this.asyncHttpClient = asyncHttpClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        HttpRequest request = null;

        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
            handleRequest(ctx, request);
        }

        if (msg instanceof HttpContent) {
            handleContent(ctx, request, (HttpContent) msg);
        }
    }

    private void handleRequest(ChannelHandlerContext ctx, HttpRequest request) {
        if (HttpUtil.is100ContinueExpected(request)) {
            send100Continue(ctx);
        }
        String destinationHost = request.headers().get("x-destination");
        RequestBuilder externalRequestBuilder = Dsl.request(request.method().name(), "https://" + destinationHost + request.uri());
        if (request.headers().contains("Content-Length")) {
            externalRequestBuilder.setBody(bodyGenerator);
        }
        asyncHttpClient.executeRequest(externalRequestBuilder.build(), new ClientAsyncHandler(ctx)).addListener(() -> {}, null);
    }

    private void handleContent(ChannelHandlerContext ctx, HttpRequest request, HttpContent httpContent) throws Exception {
        ByteBuf content = httpContent.content();

        if (content == null || content.isReadable()) {
            return;
        }
        if (httpContent instanceof LastHttpContent) {
            bodyGenerator.feed(content, true);
        } else {
            bodyGenerator.feed(content, false);
        }

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
