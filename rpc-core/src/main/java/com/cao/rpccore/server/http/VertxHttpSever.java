package com.cao.rpccore.server.http;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxHttpSever implements HttpServer {
    @Override
    public void doStart(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建 HTTP 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求
        server.requestHandler(new HttpServerHandler());

        // 启动 HTTP 服务器
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("Server is now listening on port " + port);
            } else {
                log.info("Failed to start server: " + result.cause());
            }
        });
    }
}
