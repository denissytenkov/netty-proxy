package org.proxy;

import org.proxy.server.NettyServer;

public class Main {
    public static void main(String[] arg) throws Exception {
        new NettyServer().start();
    }
}
