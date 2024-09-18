package grey.fable.rpc.framework.core.test.netty.server;

import grey.fable.rpc.framework.core.test.netty.client.RpcRequest;
import grey.fable.rpc.framework.core.test.netty.codec.NettyKryoDecoder;
import grey.fable.rpc.framework.core.test.netty.codec.NettyKryoEncoder;
import grey.fable.rpc.framework.core.test.netty.serialization.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty 服务端
 *
 * @author Fable
 * @since 2024/7/8 15:01
 */
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final int port;

    private NettyServer(int port) {
        this.port = port;
    }

    private void run() {
        // 主线程组，主要负责接收客户端的连接请求, 一般情况下只需要一个线程，除非有特殊需求（比如负载特别高的服务器），因为监听端口的操作不需要耗费太多资源
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 工作线程组，负责处理I/O 操作，包括读写数据、编解码以及业务逻辑处理. 当 bossGroup 接收到一个新的连接时，它会将该连接分配给 workerGroup 中的某个线程进行数据处理.
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        KryoSerializer kryoSerializer = new KryoSerializer();

        try {
            // 服务器启动器, 设置服务器的各种参数，包括为服务器指定 bossGroup 和 workerGroup，指定通道类型，初始化处理器（如编解码器）等
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            // 绑定端口，同步等待绑定成功
            ChannelFuture f = serverBootstrap.bind(port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("occur exception when start server:", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyServer(8889).run();
    }
}