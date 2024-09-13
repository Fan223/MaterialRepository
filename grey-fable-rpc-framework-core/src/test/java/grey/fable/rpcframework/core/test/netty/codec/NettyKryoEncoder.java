package grey.fable.rpcframework.core.test.netty.codec;

import grey.fable.rpcframework.core.test.netty.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * Netty 编码器
 *
 * @author Fable
 * @since 2024/7/8 16:00
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {

    private final Serializer serializer;

    private final Class<?> genericClass;


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)) {
            // 1. 将对象转换为 byte
            byte[] body = serializer.serialize(msg);
            // 2. 读取消息的长度
            int dataLength = body.length;
            // 3. 写入消息对应的字节数组长度, writerIndex 加 4
            out.writeInt(dataLength);
            // 4. 将字节数组写入 ByteBuf 对象中
            out.writeBytes(body);
        }
    }
}