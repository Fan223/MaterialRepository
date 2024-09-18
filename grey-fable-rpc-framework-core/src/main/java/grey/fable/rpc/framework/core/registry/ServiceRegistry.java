package grey.fable.rpc.framework.core.registry;

import grey.fable.rpc.framework.core.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 服务注册.
 *
 * @author GreyFable
 * @since 2024/8/28 10:29
 */
@SPI
public interface ServiceRegistry {

    /**
     * 注册服务到注册中心.
     *
     * @param rpcServiceName    服务名称(class name + group + version)
     * @param inetSocketAddress 服务地址
     * @author GreyFable
     * @since 2024/8/28 10:30
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
