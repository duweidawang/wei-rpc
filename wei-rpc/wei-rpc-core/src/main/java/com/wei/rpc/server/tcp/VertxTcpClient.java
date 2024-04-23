package com.wei.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.model.RpcRequest;
import com.wei.rpc.model.RpcResponse;
import com.wei.rpc.model.ServiceMetaInfo;
import com.wei.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * tcp发送客户端   用来发起tcp请求
 */
public class VertxTcpClient {


    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        //1 创建一个Tcp客户端
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        //2 使用netClient客户端发送请求是异步，通过CompletableFuture改为同步，等待响应结果
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        //3 通过客户端连接对应服务的ip 加端口 进行tcp连接
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                //4 判断是否连接成功，成功后才开始发送数据
                result -> {
                    if (!result.succeeded()) {
                        System.err.println("Failed to connect to TCP server");
                        return;
                    }
                    NetSocket socket = result.result();

                    // 5 因为使用了传输层的tcp协议，应用程没有使用具体协议，
                    // 所以自己定义了一个协议（避免使用像http包含太多额外字段信息） 包含请求头与请求体
                    //封装协议对象  其实就是设置请求头与请求体
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    try {
                        //6因为使用了Vertx的Tcp来接受的时候接受与发送的是一个Buffer对象 缓冲区
                        //所以需要将发送的内容编码为字节放入缓冲区，然后调用api发送数据
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }

                    // 7发送数据后需要等待响应内容
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    //8响应的内容也是一个缓冲区对象，其实就是将收到的字节序列放到缓冲区，
                                    // 然后从缓冲区读取数据
                                    //所以也需要先将缓冲区的字节序列转化为协议对象
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                            (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    //9等待获取协议对像中的请求体，得到响应内容
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);

                });

        RpcResponse rpcResponse = responseFuture.get();
        // 10记得关闭连接
        netClient.close();
        //11 响应获取的对象信息
        return rpcResponse;
    }




















//    public void start() {
//        // 创建 Vert.x 实例
//        Vertx vertx = Vertx.vertx();
//        vertx.createNetClient().connect(8888, "localhost", netSocketAsyncResult -> {
//            if (netSocketAsyncResult.succeeded()) {
//                io.vertx.core.net.NetSocket socket = netSocketAsyncResult.result();
//                for(int i=0;i<1000;i++){
//                    socket.write("Hello server,Hello server,Hello server,Hello server,Hello server");
//                }
//
//                socket.handler(buffer -> {
//                    System.out.println(buffer.toString());
//                });
//            } else {
//                System.out.println("failed to connect to tcp server");
//            }
//        });
//    }
//
//    public static void main(String[] args) {
//        new VertxTcpClient().start();
//    }

}
