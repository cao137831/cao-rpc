package com.cao.rpccore.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.cao.rpccore.RpcApplication;
import com.cao.rpccore.model.RpcRequest;
import com.cao.rpccore.model.RpcResponse;
import com.cao.rpccore.model.ServiceMetaInfo;
import com.cao.rpccore.protocol.ProtocolConstant;
import com.cao.rpccore.protocol.ProtocolMessage;
import com.cao.rpccore.protocol.ProtocolMessageDecoder;
import com.cao.rpccore.protocol.ProtocolMessageEncoder;
import com.cao.rpccore.protocol.enums.ProtocolMessageSerializerEnum;
import com.cao.rpccore.protocol.enums.ProtocolMessageTypeEnum;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class VertxTcpClient {
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        // 发送 TCP 请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
            if (result.succeeded()) {
                log.info("Connected to TCP server");
                NetSocket socket = result.result();
                // 发送数据
                // 构造消息
                ProtocolMessage<RpcRequest> requestProtocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                header.setSerializerKey((byte) ProtocolMessageSerializerEnum.getEnmByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                // 生成全局请求 ID
                header.setRequestId(IdUtil.getSnowflakeNextId());
                requestProtocolMessage.setHeader(header);
                requestProtocolMessage.setBody(rpcRequest);

                // 编码请求
                try {
                    Buffer encode = ProtocolMessageEncoder.encode(requestProtocolMessage);
                    socket.write(encode);
                } catch (IOException e) {
                    throw new RuntimeException("协议消息编码错误");
                }

                // 接收响应
                TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                    try {
                        ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        // todo  由于 VErt.x 提供的是异步、反应式处理，使用 CompletableFuture 转异步为同步
                        // 完成了响应
                        responseFuture.complete(responseProtocolMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息解码错误");
                    }
                });
                socket.handler(bufferHandlerWrapper);
            } else {
                log.error("Failed to connect to TCP server");
                return;
            }
        });
        // 阻塞，直到响应完成，才会继续向下执行
        RpcResponse rpcResponse = responseFuture.get();
        // 关闭连接
        netClient.close();
        return rpcResponse;
    }
}
