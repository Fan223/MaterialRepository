package grey.fable.rpcframework.core.provider.impl;

import grey.fable.base.net.NetUtil;
import grey.fable.rpcframework.core.config.RpcServiceConfig;
import grey.fable.rpcframework.core.enums.RpcErrorMessageEnum;
import grey.fable.rpcframework.core.exception.RpcException;
import grey.fable.rpcframework.core.extension.ExtensionLoader;
import grey.fable.rpcframework.core.provider.ServiceProvider;
import grey.fable.rpcframework.core.registry.ServiceRegistry;
import grey.fable.rpcframework.core.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zookeeper 实现类.
 *
 * @author GreyFable
 * @since 2024/8/29 9:56
 */
@Slf4j
public class ZkServiceProviderImpl implements ServiceProvider {

    /**
     * 存储服务的 {@link Map}.
     * Key: 服务名(interface name + group + version).
     * Value: 服务对象.
     */
    private final Map<String, Object> serviceMap;

    /**
     * 已注册的服务.
     */
    private final Set<String> registeredService;

    /**
     * 服务注册, {@link ServiceRegistry}.
     */
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }

    /**
     * 发布服务.
     *
     * @param rpcServiceConfig {@link RpcServiceConfig}
     * @author GreyFable
     * @since 2024/9/5 15:56
     */
    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        try {
            String localHostAddress = NetUtil.getLocalHostAddress();
            this.addService(rpcServiceConfig);
            // 注册服务.
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(),
                    new InetSocketAddress(localHostAddress, NettyRpcServer.PORT));
        } catch (UnknownHostException e) {
            log.error("获取本地主机 IP 地址时发生异常", e);
        }
    }

    /**
     * 添加服务到本地缓存.
     *
     * @param rpcServiceConfig {@link RpcServiceConfig}
     * @author GreyFable
     * @since 2024/9/9 10:02
     */
    private void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        // 不存在则添加.
        if (!registeredService.contains(rpcServiceName)) {
            registeredService.add(rpcServiceName);
            serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
            log.info("添加服务: {} 和接口:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
        }
    }

    /**
     * 从本地缓存获取服务.
     *
     * @param rpcServiceName 服务名称
     * @return {@link Object}
     * @author GreyFable
     * @since 2024/9/9 10:03
     */
    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
