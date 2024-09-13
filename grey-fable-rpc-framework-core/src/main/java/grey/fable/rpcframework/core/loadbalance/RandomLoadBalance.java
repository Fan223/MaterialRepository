package grey.fable.rpcframework.core.loadbalance;

import grey.fable.rpcframework.core.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡策略.
 *
 * @author GreyFable
 * @since 2024/8/28 11:20
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    private final Random random = new Random();

    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
