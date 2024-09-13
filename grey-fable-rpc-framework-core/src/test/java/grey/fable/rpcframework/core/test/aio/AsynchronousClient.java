package grey.fable.rpcframework.core.test.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 异步 IO 客户端
 *
 * @author Fan
 * @since 2023/5/9 16:21
 */
public class AsynchronousClient {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        Future<?> future = client.connect(new InetSocketAddress("127.0.0.1", 8080));
        // 阻塞一下，等待连接成功
        future.get();

        Attachment att = new Attachment();
        att.setClient(client);
        att.setReadMode(false);
        att.setBuffer(ByteBuffer.allocate(2048));
        byte[] data = "I am obot!".getBytes();
        att.getBuffer().put(data);
        att.getBuffer().flip();

        // 异步发送数据到服务端
        client.write(att.getBuffer(), att, new ClientChannelHandler());

        // 这里休息一下再退出，给出足够的时间处理数据
        Thread.sleep(2000);
    }
}
