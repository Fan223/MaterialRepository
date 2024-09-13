package grey.fable.rpcframework.core.remoting.transport.netty.client;

import grey.fable.base.factory.SingletonFactory;
import grey.fable.rpcframework.core.enums.CompressTypeEnum;
import grey.fable.rpcframework.core.enums.SerializationTypeEnum;
import grey.fable.rpcframework.core.extension.ExtensionLoader;
import grey.fable.rpcframework.core.registry.ServiceDiscovery;
import grey.fable.rpcframework.core.remoting.consts.RpcConstants;
import grey.fable.rpcframework.core.remoting.dto.RpcMessage;
import grey.fable.rpcframework.core.remoting.dto.RpcRequest;
import grey.fable.rpcframework.core.remoting.dto.RpcResponse;
import grey.fable.rpcframework.core.remoting.transport.RpcRequestTransport;
import grey.fable.rpcframework.core.remoting.transport.netty.codec.RpcMessageDecoder;
import grey.fable.rpcframework.core.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Netty 客户端.
 *
 * @author GreyFable
 * @since 2024/8/28 10:07
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {
        // 初始化资源, 如 EventLoopGroup、Bootstrap
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接的超时时间，如果超过此时间或无法建立连接，则连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        // 如果在 15 秒内没有数据发送到服务器，则发送心跳请求
                        channelPipeline.addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        // 自定义序列化编解码器
                        channelPipeline.addLast(new RpcMessageEncoder());
                        channelPipeline.addLast(new RpcMessageDecoder());
                        // 自定义客户端处理器.
                        channelPipeline.addLast(new NettyRpcClientHandler());
                    }
                });
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    /**
     * 传输 rpc 请求(RpcRequest) 到服务端.
     *
     * @param rpcRequest {@link RpcRequest}
     * @return {@link Object}
     * @author GreyFable
     * @since 2024/9/10 17:05
     */
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // 返回值
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // 查找服务器地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        // 获取服务器地址相关通道
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            // 保存未处理的请求
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder().data(rpcRequest)
                    .codec(SerializationTypeEnum.KYRO.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();

            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("客户端发送消息: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("发送失败:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * 连接服务器并获取通道，以便将 RPC 消息发送到服务器.
     *
     * @param inetSocketAddress {@link InetSocketAddress}
     * @return {@link Channel}
     * @author GreyFable
     * @since 2024/8/28 10:21
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("客户端连接 [{}] 成功!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
}