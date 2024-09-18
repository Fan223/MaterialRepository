package grey.fable.rpc.framework.core.test.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 异步 IO 服务端处理器.
 *
 * @author Fan
 * @since 2023/5/9 15:45
 */
@Slf4j
public class ServerChannelHandler implements CompletionHandler<Integer, Attachment> {

    @Override
    public void completed(Integer result, Attachment att) {
        if (att.isReadMode()) {
            // 读取来自客户端的数据
            ByteBuffer buffer = att.getBuffer();
            buffer.flip();
            byte[] bytes = new byte[buffer.limit()];
            buffer.get(bytes);
            String msg = new String(buffer.array()).trim();
            log.info("收到来自客户端的数据: " + msg);

            // 响应客户端请求，返回数据
            buffer.clear();
            buffer.put("Response from server!".getBytes(Charset.forName(StandardCharsets.UTF_8.toString())));
            att.setReadMode(false);
            buffer.flip();
            // 写数据到客户端也是异步
            att.getClient().write(buffer, att, this);
        } else {
            // 到这里，说明往客户端写数据也结束了，有以下两种选择:
            // 1. 继续等待客户端发送新的数据过来
//            att.setReadMode(true);
//            att.getBuffer().clear();
//            att.getClient().read(att.getBuffer(), att, this);
            try {
                // 2. 既然服务端已经返回数据给客户端，断开这次的连接
                att.getClient().close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        log.info("连接断开");
    }
}
