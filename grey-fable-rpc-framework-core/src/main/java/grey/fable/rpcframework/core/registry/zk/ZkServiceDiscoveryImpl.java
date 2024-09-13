package grey.fable.rpcframework.core.registry.zk;

import grey.fable.base.collection.CollectionUtil;
import grey.fable.rpcframework.core.enums.RpcErrorMessageEnum;
import grey.fable.rpcframework.core.exception.RpcException;
import grey.fable.rpcframework.core.extension.ExtensionLoader;
import grey.fable.rpcframework.core.loadbalance.LoadBalance;
import grey.fable.rpcframework.core.registry.ServiceDiscovery;
import grey.fable.rpcframework.core.utils.CuratorUtils;
import grey.fable.rpcframework.core.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Zookeeper 服务发现.
 *
 * @author GreyFable
 * @since 2024/8/28 10:52
 */
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("consistenthash");
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        // 服务地址
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // 负载均衡
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("成功找到服务地址:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
