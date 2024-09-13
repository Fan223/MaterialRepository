package grey.fable.server.test;

import grey.fable.api.HelloService;
import grey.fable.rpcframework.core.config.RpcServiceConfig;
import grey.fable.rpcframework.core.remoting.transport.netty.server.NettyRpcServer;
import grey.fable.server.service.impl.HelloServiceImpl;

/**
 * 手动服务端启动类.
 *
 * @author GreyFable
 * @since 2024/9/3 9:32
 */
public class ManualNettyServerApp {
    public static void main(String[] args) {
        // 创建 Netty 服务端.
        NettyRpcServer nettyRpcServer = new NettyRpcServer();
        // 创建 HelloService 实例.
        HelloService helloService = new HelloServiceImpl();
        // 构建服务配置.
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("test1").version("version1").service(helloService).build();
        // 注册服务.
        nettyRpcServer.registerService(rpcServiceConfig);
        // 启动 Netty 服务端.
        nettyRpcServer.start();
    }
}
