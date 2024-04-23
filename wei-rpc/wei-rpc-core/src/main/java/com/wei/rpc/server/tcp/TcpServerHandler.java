package com.wei.rpc.server.tcp;
import com.wei.rpc.config.RpcApplication;
import com.wei.rpc.model.RpcRequest;
import com.wei.rpc.model.RpcResponse;
import com.wei.rpc.protocol.ProtocolMessage;
import com.wei.rpc.protocol.ProtocolMessageDecoder;
import com.wei.rpc.protocol.ProtocolMessageEncoder;
import com.wei.rpc.protocol.ProtocolMessageTypeEnum;
import com.wei.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * tcp服务端请求处理器
 */
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {
        //装饰者模式
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {

            //1 需要将buffer中的字节 通过解码器转化为一个协议对象
                ProtocolMessage<RpcRequest> protocolMessage;
                try {
                    protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
                } catch (IOException e) {
                    throw new RuntimeException("协议消息解码错误");
                }
                //2 通过协议对像获取请求体内容
                RpcRequest rpcRequest = protocolMessage.getBody();

                //3 构建一个响应对象
                RpcResponse rpcResponse = new RpcResponse();
                if(rpcRequest == null){
                    rpcResponse.setMessage("body data is null");
                    return;
                }
                try {
                    //4 根据请求体中的内容获取请求对象 得到具体方法
                    String methodName = rpcRequest.getMethodName();
                    //5 根据方法的内对象在本地注册中心中获取具体的类对象
                    Class<?> aClass = LocalRegistry.get(rpcRequest.getServiceName());
                    //6 根据方法，参数，参数类型获取方法对象
                    Method method = aClass.getMethod(methodName,rpcRequest.getRequestParamTypes());
                    // 7 反射调用具体方法后获得结果
                    Object invoke = method.invoke(aClass.newInstance(), rpcRequest.getArgs());
                    //8 得到结果后封装为响应对象   但还要经过序列化和编码器
                    rpcResponse.setData(invoke);
                    rpcResponse.setDataType(method.getReturnType());
                    rpcResponse.setMessage("ok");
                } catch (Exception e) {
                    e.printStackTrace();
                    rpcResponse.setMessage(e.getMessage());
                    rpcResponse.setExcption(e);
                }
               // 9 响应内容
                //10 请求头复用发送过来的
                ProtocolMessage.Header header = protocolMessage.getHeader();
                //11 但需要更改请求类型改为response，原本是request
                header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
                //12 也需要通过编码器 首先设置协议对象的请求头以及请求体
                ProtocolMessage<RpcResponse> rpcRequestProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
                try {
                    //13 然后encode 然后发送
                    Buffer encode = ProtocolMessageEncoder.encode(rpcRequestProtocolMessage);
                    netSocket.write(encode);

                } catch (IOException e) {
                    throw new RuntimeException("协议消息编码错误");
                }
        });

        netSocket.handler(tcpBufferHandlerWrapper);

        }
}
