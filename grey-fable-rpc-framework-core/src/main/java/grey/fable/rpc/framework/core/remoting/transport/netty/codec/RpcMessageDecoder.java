package grey.fable.rpc.framework.core.remoting.transport.netty.codec;

import grey.fable.rpc.framework.core.enums.CompressTypeEnum;
import grey.fable.rpc.framework.core.enums.SerializationTypeEnum;
import grey.fable.rpc.framework.core.extension.ExtensionLoader;
import grey.fable.rpc.framework.core.remoting.constant.RpcConstants;
import grey.fable.rpc.framework.core.remoting.dto.RpcMessage;
import grey.fable.rpc.framework.core.remoting.dto.RpcRequest;
import grey.fable.rpc.framework.core.remoting.dto.RpcResponse;
import grey.fable.rpc.framework.core.compress.Compress;
import grey.fable.rpc.framework.core.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * RPC 消息解码器.
 *
 * @author GreyFable
 * @since 2024/8/28 17:26
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        // lengthFieldOffset, 长度字段的偏移量: 魔数 4B, 版本号 1B, 所以值为 5.
        // lengthFieldLength, 长度字段的长度: 总长度 4B, 所以值为 4.
        // lengthAdjustment, 添加到长度字段值中的补偿值: 完整长度包含所有数据, 并且之前读取了 9 个字节, 因此剩余长度是 fullLength - 9, 所以值为 -9.
        // initialBytesToStrip: 需要从解码帧中剥离的前几个字节的数量: 因为将手动检查魔数和版本号，因此不需要剥离任何字节, 所以值为 0.
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf frame && frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
            try {
                return decodeFrame(frame);
            } catch (Exception e) {
                log.error("解码数据帧错误!", e);
                throw e;
            } finally {
                frame.release();
            }

        }
        return decoded;
    }

    /**
     * 解码数据帧, 必须按顺序读取.
     *
     * @param in {@link ByteBuf}
     * @return {@link Object}
     * @author GreyFable
     * @since 2024/9/9 15:00
     */
    private Object decodeFrame(ByteBuf in) {
        // 检查魔数
        checkMagicNumber(in);
        // 检查版本
        checkVersion(in);
        // 读取完整长度
        int fullLength = in.readInt();
        // 读取消息类型
        byte messageType = in.readByte();
        // 读取序列化类型
        byte codecType = in.readByte();
        // 读取解压类型
        byte compressType = in.readByte();
        // 读取请求 ID
        int requestId = in.readInt();
        // 生成 RpcMessage 对象
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        // 心跳请求消息
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        // 心跳响应消息
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        // 计算消息体长度, 总长度 - 消息头长度.
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // 解压字节
            String compressName = CompressTypeEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bs = compress.decompress(bs);
            // 反序列化对象
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("序列化类型名称: [{}] ", codecName);
            Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest rpcRequest = serialization.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(rpcRequest);
            } else {
                RpcResponse rpcResponse = serialization.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(rpcResponse);
            }
        }
        return rpcMessage;
    }

    /**
     * 检查魔数.
     *
     * @param in {@link ByteBuf}
     * @author GreyFable
     * @since 2024/8/29 9:45
     */
    private void checkMagicNumber(ByteBuf in) {
        // 读取魔数, 即前 4 位, 然后进行比较
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("未知魔数: " + Arrays.toString(tmp));
            }
        }
    }

    /**
     * 检查版本号.
     *
     * @param in {@link ByteBuf}
     * @author GreyFable
     * @since 2024/8/29 9:45
     */
    private void checkVersion(ByteBuf in) {
        // 读取版本号并进行比较
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("版本不兼容: " + version);
        }
    }
}
