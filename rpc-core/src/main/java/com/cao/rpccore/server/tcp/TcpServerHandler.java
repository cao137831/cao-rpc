package com.cao.rpccore.server.tcp;

import com.cao.rpccore.model.RpcRequest;
import com.cao.rpccore.model.RpcResponse;
import com.cao.rpccore.protocol.ProtocolMessage;
import com.cao.rpccore.protocol.ProtocolMessageDecoder;
import com.cao.rpccore.protocol.ProtocolMessageEncoder;
import com.cao.rpccore.protocol.enums.ProtocolMessageStatusEnum;
import com.cao.rpccore.protocol.enums.ProtocolMessageTypeEnum;
import com.cao.rpccore.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {

        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 处理请求代码
            ProtocolMessage<RpcRequest> requestProtocolMessage;
            try {
                requestProtocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = requestProtocolMessage.getBody();

            // 处理请求
            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            try {
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            // 发送响应，编码
            ProtocolMessage.Header header = requestProtocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });
        netSocket.handler(bufferHandlerWrapper);

        // 处理连接
//        netSocket.handler(buffer -> {
//            // 接收请求，解码
//            ProtocolMessage<RpcRequest> requestProtocolMessage;
//            try {
//                requestProtocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
//            } catch (IOException e) {
//                throw new RuntimeException("协议消息解码错误");
//            }
//            RpcRequest rpcRequest = requestProtocolMessage.getBody();
//
//            // 处理请求
//            // 构造响应结果对象
//            RpcResponse rpcResponse = new RpcResponse();
//            try {
//                // 获取要调用的服务实现类，通过反射调用
//                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
//                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
//                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
//                // 封装返回结果
//                rpcResponse.setData(result);
//                rpcResponse.setDataType(method.getReturnType());
//                rpcResponse.setMessage("ok");
//            } catch (Exception e) {
//                e.printStackTrace();
//                rpcResponse.setMessage(e.getMessage());
//                rpcResponse.setException(e);
//            }
//
//            // 发送响应，编码
//            ProtocolMessage.Header header = requestProtocolMessage.getHeader();
//            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
//            header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
//            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
//            try {
//                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
//                netSocket.write(encode);
//            } catch (IOException e) {
//                throw new RuntimeException("协议消息编码错误");
//            }
//        });
    }
}
