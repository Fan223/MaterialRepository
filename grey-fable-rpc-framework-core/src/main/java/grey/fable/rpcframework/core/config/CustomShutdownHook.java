package grey.fable.rpcframework.core.config;

import grey.fable.rpcframework.core.utils.CuratorUtils;
import grey.fable.rpcframework.core.remoting.transport.netty.server.NettyRpcServer;
import grey.fable.rpcframework.core.utils.ThreadPoolFactoryUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 当服务器关闭时，执行诸如取消注册所有服务之类的操作.
 *
 * @author GreyFable
 * @since 2024/8/29 14:51
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("添加 ShutdownHook 用于全部清除");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {
                // ignore
            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}
