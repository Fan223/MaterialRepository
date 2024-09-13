package grey.fable.rpcframework.core.remoting.transport.netty.codec;

import grey.fable.rpcframework.core.compress.Compress;
import grey.fable.rpcframework.core.enums.CompressTypeEnum;
import grey.fable.rpcframework.core.enums.SerializationTypeEnum;
import grey.fable.rpcframework.core.extension.ExtensionLoader;
import grey.fable.rpcframework.core.remoting.consts.RpcConstants;
import grey.fable.rpcframework.core.remoting.dto.RpcMessage;
import grey.fable.rpcframework.core.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义协议解码器.
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 *
 * @author GreyFable
 * @since 2024/8/28 16:57
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        // 写入魔数
        out.writeBytes(RpcConstants.MAGIC_NUMBER);
        // 写入版本号
        out.writeByte(RpcConstants.VERSION);
        // 留一个地方写入完整长度, 需要后续计算.
        out.writerIndex(out.writerIndex() + 4);
        // 写入消息类型
        byte messageType = rpcMessage.getMessageType();
        out.writeByte(messageType);
        // 写入序列化类型
        out.writeByte(rpcMessage.getCodec());
        // 写入压缩类型
        out.writeByte(CompressTypeEnum.GZIP.getCode());
        // 写入请求 ID
        out.writeInt(ATOMIC_INTEGER.getAndIncrement());
        // 构建完整长度
        int fullLength = RpcConstants.HEAD_LENGTH;
        byte[] bodyBytes = null;
        // 如果不是心跳消息, 则 fullLength = head length + body length.
        if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            // 序列化对象
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("序列化类型名称: [{}] ", codecName);
            Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(codecName);
            bodyBytes = serialization.serialize(rpcMessage.getData());
            // 压缩字节
            String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bodyBytes = compress.compress(bodyBytes);
            fullLength += bodyBytes.length;
        }
        // 写入消息体
        if (null != bodyBytes) {
            out.writeBytes(bodyBytes);
        }
        // 将句柄退回到之前留的写入完整长度的位置.
        int writeIndex = out.writerIndex();
        out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
        // 写入完整长度.
        out.writeInt(fullLength);
        out.writerIndex(writeIndex);
    }
}
