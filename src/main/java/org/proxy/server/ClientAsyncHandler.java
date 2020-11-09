package org.proxy.server;

import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseStatus;

import java.nio.ByteBuffer;

public class ClientAsyncHandler implements AsyncHandler {

    private FullHttpResponse response;
    private ChannelHandlerContext ctx;

    public ClientAsyncHandler(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onThrowable(Throwable t) {
        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR);
        ctx.write(response);
    }

    @Override
    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
        System.out.println("body part");
        ByteBuffer content = bodyPart.getBodyByteBuffer();
        ctx.write(new DefaultHttpContent(Unpooled.wrappedBuffer(content)));
        return State.CONTINUE;

    }

    @Override
    public State onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
        System.out.println("status");
        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, io.netty.handler.codec.http.HttpResponseStatus.valueOf(responseStatus.getStatusCode()));
        ctx.write(response);
        return State.CONTINUE;
    }

    @Override
    public State onHeadersReceived(HttpHeaders headers) throws Exception {
        System.out.println("headers");
        headers.forEach(header -> {
            System.out.println(header.getKey() + ":" + header.getValue());
            response.headers().set(header.getKey(), header.getValue());
        });
        return State.CONTINUE;
    }

    @Override
    public Object onCompleted() throws Exception {
        System.out.println("complete");
        ctx.write(new DefaultLastHttpContent(Unpooled.buffer()));
        ctx.flush();
        return new Object();
    }
}
