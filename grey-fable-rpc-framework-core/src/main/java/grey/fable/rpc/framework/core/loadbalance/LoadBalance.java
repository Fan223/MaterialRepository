package grey.fable.rpc.framework.core.loadbalance;

import grey.fable.rpc.framework.core.extension.SPI;
import grey.fable.rpc.framework.core.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡策略接口.
 *
 * @author GreyFable
 * @since 2024/8/28 10:54
 */
@SPI
public interface LoadBalance {

    /**
     * 从现有服务地址列表中选择一个.
     *
     * @param serviceAddresses 服务地址列表
     * @param rpcRequest       {@link RpcRequest}
     * @return {@link String}
     * @author GreyFable
     * @since 2024/8/28 11:18
     */
    String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest);
}
