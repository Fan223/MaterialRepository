package grey.fable.rpc.framework.core.registry.zk;

import grey.fable.rpc.framework.core.registry.ServiceRegistry;
import grey.fable.rpc.framework.core.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * Zookeeper 服务注册.
 *
 * @author GreyFable
 * @since 2024/8/28 10:32
 */
public class ZkServiceRegistryImpl implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        // 获取节点服务路径, "/服务名/IP 地址"
        String servicePath = "/" + rpcServiceName + inetSocketAddress.toString();
        // 获取客户端
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        // 创建持久化节点
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
