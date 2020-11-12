package org.proxy;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.proxy.server.NettyServer;

import javax.net.ssl.SSLException;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class Main {
    public static void main(String[] arg) throws Exception {
        new NettyServer().start(createClient());
    }

    private static AsyncHttpClient createClient() {
        try {
            AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder()
                    .setSslContext(SslContextBuilder
                            .forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .clientAuth(ClientAuth.NONE)
                            .sslProvider(SslProvider.JDK).build())
                    .setMaxRequestRetry(1)
                    .setMaxConnections(500)
                    .setMaxConnectionsPerHost(200)
                    .setResponseBodyPartFactory(AsyncHttpClientConfig.ResponseBodyPartFactory.LAZY)
                    .build();
            return asyncHttpClient(config);
        } catch (SSLException e) {
            throw new SecurityException(e);
        }
    }
}
