package grey.fable.rpc.framework.core.provider;

import grey.fable.rpc.framework.core.extension.SPI;
import grey.fable.rpc.framework.core.config.RpcServiceConfig;

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
