package com.cao.rpccore.protocol;

import com.cao.rpccore.model.RpcRequest;
import com.cao.rpccore.model.RpcResponse;
import com.cao.rpccore.protocol.enums.ProtocolMessageSerializerEnum;
import com.cao.rpccore.protocol.enums.ProtocolMessageTypeEnum;
import com.cao.rpccore.serializer.Serializer;
import com.cao.rpccore.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 消息解码器
 */
public class ProtocolMessageDecoder {

    /**
     * 解码
     *
     * @param buffer
     * @return
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("消息 magic 非法");
        }
        byte version = buffer.getByte(1);
        byte serializerKey = buffer.getByte(2);
        byte type = buffer.getByte(3);
        byte status = buffer.getByte(4);
        long requestId = buffer.getLong(5);
        int len = buffer.getInt(13);

        header.setMagic(magic);
        header.setVersion(version);
        header.setSerializerKey(serializerKey);
        header.setType(type);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setBodyLength(len);
        // 解决粘包问题，只读指定长度的数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + len);

        // 获取序列化器，校验 serializerKey
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnmByKey(header.getSerializerKey());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化消息的协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        // 校验 type
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnmByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("序列化消息的类型不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, rpcRequest);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, rpcResponse);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持的消息类型");
        }
    }
}
