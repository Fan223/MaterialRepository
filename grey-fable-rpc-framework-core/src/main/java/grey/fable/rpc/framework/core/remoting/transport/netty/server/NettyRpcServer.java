package grey.fable.rpc.framework.core.remoting.transport.netty.server;

import grey.fable.base.net.NetUtils;
import grey.fable.base.util.RuntimeUtils;
import grey.fable.rpc.framework.core.config.CustomShutdownHook;
import grey.fable.rpc.framework.core.extension.ExtensionLoader;
import grey.fable.rpc.framework.core.remoting.transport.netty.codec.RpcMessageDecoder;
import grey.fable.rpc.framework.core.config.RpcServiceConfig;
import grey.fable.rpc.framework.core.provider.ServiceProvider;
import grey.fable.rpc.framework.core.remoting.transport.netty.codec.RpcMessageEncoder;
import grey.fable.rpc.framework.core.util.ThreadPoolFactoryUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Netty 服务端.
 *
 * @author GreyFable
 * @since 2024/8/29 11:56
 */
@Slf4j
@Component
public class NettyRpcServer {

    public static final int PORT = 9998;

    private final ServiceProvider serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension("zk");

    /**
     * 注册服务.
     *
     * @param rpcServiceConfig {@link RpcServiceConfig}
     * @author GreyFable
     * @since 2024/9/9 10:05
     */
    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    @SneakyThrows
    public void start() {
        // 添加结束清除钩子
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 处理非 I/O 相关的任务，比如耗时的业务逻辑或一些需要在单独的线程中处理的任务, 这里用来处理消息
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtils.getProcessors() * 2,
                ThreadPoolFactoryUtils.createThreadFactory("service-handler-group", false)
        );
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP 默认开启了 Nagle 算法, 用于尽可能的发送大数据块, 减少网络传输. 使用 TCP_NODELAY 参数控制是否启用 Nagle 算法.
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度, 如果连接建立频繁, 服务器处理创建新连接较慢, 可以适当调大这个参数.
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            // 处理器链
                            ChannelPipeline pipeline = channel.pipeline();
                            // 30 秒之内没有收到客户端请求的话就关闭连接.
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            // 自定义序列化编解码器
                            pipeline.addLast(new RpcMessageEncoder());
                            pipeline.addLast(new RpcMessageDecoder());
                            pipeline.addLast(serviceHandlerGroup, new NettyRpcServerHandler());
                        }
                    });

            // 绑定端口, 同步等待绑定成功.
            ChannelFuture future = bootstrap.bind(NetUtils.getLocalHostAddress(), PORT).sync();
            // 等待服务端监听端口关闭.
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务器时发生异常: ", e);
            Thread.currentThread().interrupt();
        } finally {
            log.error("关闭 bossGroup 和 workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }
}
