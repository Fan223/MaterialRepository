package grey.fable.rpcframework.core.loadbalance;

import grey.fable.base.collection.CollectionUtil;
import grey.fable.rpcframework.core.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡策略抽象类.
 *
 * @author GreyFable
 * @since 2024/8/28 11:23
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest) {
        // 服务器为 0/1 个时直接返回
        if (CollectionUtil.isEmpty(serviceAddresses)) {
            return null;
        } else if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);
}
