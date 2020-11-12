package org.proxy.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseStatus;

import java.nio.ByteBuffer;

public class ClientAsyncHandler implements AsyncHandler {

    private HttpResponse response;
    private ChannelHandlerContext ctx;

    public ClientAsyncHandler(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onThrowable(Throwable t) {
        response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR);
        ctx.write(response);
    }

    @Override
    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
        ByteBuffer content = bodyPart.getBodyByteBuffer();
        ctx.write(new DefaultHttpContent(Unpooled.wrappedBuffer(content)));
        return State.CONTINUE;

    }

    @Override
    public State onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
        response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, io.netty.handler.codec.http.HttpResponseStatus.valueOf(responseStatus.getStatusCode()));
        ctx.write(response);
        return State.CONTINUE;
    }

    @Override
    public State onHeadersReceived(HttpHeaders headers) throws Exception {
        headers.forEach(header -> {
            response.headers().set(header.getKey(), header.getValue());
        });
        return State.CONTINUE;
    }

    @Override
    public Object onCompleted() throws Exception {
        ctx.write(new DefaultLastHttpContent(Unpooled.buffer()));
        ctx.flush();
        ctx.close();
        return new Object();
    }
}
