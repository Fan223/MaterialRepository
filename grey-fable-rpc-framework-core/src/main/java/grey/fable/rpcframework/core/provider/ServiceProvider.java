package grey.fable.rpcframework.core.provider;

import grey.fable.rpcframework.core.config.RpcServiceConfig;
import grey.fable.rpcframework.core.extension.SPI;

/**
 * 存储并提供服务对象.
 *
 * @author GreyFable
 * @since 2024/8/29 9:51
 */
@SPI
public interface ServiceProvider {

    /**
     * 发布服务.
     *
     * @param rpcServiceConfig {@link RpcServiceConfig}
     * @author GreyFable
     * @since 2024/8/29 9:55
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

    /**
     * 获取服务.
     *
     * @param rpcServiceName RpcServiceName
     * @return {@link Object}
     * @author GreyFable
     * @since 2024/8/29 9:55
     */
    Object getService(String rpcServiceName);
}
