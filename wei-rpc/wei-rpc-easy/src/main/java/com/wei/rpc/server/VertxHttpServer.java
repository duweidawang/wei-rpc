package com.wei.rpc.server;

import com.wei.rpc.server.HttpServer;
import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        //创建一个vertx实例
        Vertx vertx = Vertx.vertx();
        //创建一个http服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();
//        httpServer.requestHandler(httpServerRequest -> {
//            //处理http请求
//            System.out.println("Received request :" + httpServerRequest.method()+ " "+httpServerRequest.uri());
//            //发送HTTP响应
//            httpServerRequest.response()
//                            .putHeader("content-type", "text/plain")
//                            .end("Hello from Vert.x!");
//        });
        //设置请求处理器
        httpServer.requestHandler(new HttpServerHandler());

        httpServer.listen(port,result->{
            if (result.succeeded()){
                System.out.println("Server is now listening on port : "+ port);
            }else{
                System.out.println("Failed to start server : "+result.cause());
            }
        });

    }
}
