package grey.fable.rpc.framework.core.registry;

import grey.fable.rpc.framework.core.extension.SPI;
import grey.fable.rpc.framework.core.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现.
 *
 * @author GreyFable
 * @since 2024/8/28 10:29
 */
@SPI
public interface ServiceDiscovery {

    /**
     * 根据 rpcServiceName 获取远程服务地址.
     *
     * @param rpcRequest 完整的服务名称（class name+group+version）
     * @return {@link InetSocketAddress}
     * @author GreyFable
     * @since 2024/8/28 10:31
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
