package grey.fable.server;

import grey.fable.rpcframework.core.annotation.RpcScan;
import grey.fable.rpcframework.core.remoting.transport.netty.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 服务端启动类.
 *
 * @author GreyFable
 * @since 2024/8/29 15:38
 */
@RpcScan(basePackage = "grey.fable")
public class NettyServerApp {
    public static void main(String[] args) {
        // 通过注解注册服务.
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerApp.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        nettyRpcServer.start();
    }
}
