package grey.fable.rpcframework.core.test.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 异步 IO 服务端.
 *
 * @author Fan
 * @since 2023/5/9 15:39
 */
@Slf4j
public class AsynchronousServer {
    public static void main(String[] args) throws IOException {
        // 实例化, 并监听端口.
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(8080));

        Attachment attachment = new Attachment();
        attachment.setServer(server);

        server.accept(attachment, new CompletionHandler<>() {
            @Override
            public void completed(AsynchronousSocketChannel client, Attachment att) {
                try {
                    log.info("收到新的连接: " + client.getRemoteAddress());
                    // 收到新的连接后, server 应该重新调用 accept 方法等待新的连接进来
                    att.getServer().accept(att, this);

                    Attachment newAtt = new Attachment();
                    newAtt.setServer(server);
                    newAtt.setClient(client);
                    newAtt.setReadMode(true);
                    newAtt.setBuffer(ByteBuffer.allocate(2048));

                    client.read(newAtt.getBuffer(), newAtt, new ServerChannelHandler());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable t, Attachment att) {
                log.info("accept failed");
            }
        });

        // 为了防止 main 线程退出
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
