package grey.fable.rpc.framework.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Curator 工具类.
 *
 * @author GreyFable
 * @since 2024/8/28 10:34
 */
@Slf4j
public final class CuratorUtils {

    // ZK 客户端.
    private static CuratorFramework zkClient;

    // 已注册的节点路径.
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    // 服务地址缓存.
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    private CuratorUtils() {
    }

    public static CuratorFramework getZkClient() {
        String zookeeperAddress = "124.222.118.90:2181";
        // 如果zkClient已经启动了，直接返回
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        // 重试策略。重试3次，并将增加两次重试之间的睡眠时间.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                // 会话超时时间
                .sessionTimeoutMs(5000)
                // 连接超时时间
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        try {
            // 等待30s，直到连接到zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("连接 ZK 超时!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }

    /**
     * 创建持久节点。与临时节点不同，当客户端断开连接时，不会删除持久性节点.
     *
     * @param zkClient {@link CuratorFramework}
     * @param path     节点路径
     * @author GreyFable
     * @since 2024/8/28 10:46
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("节点已存在. 节点为:[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("节点成功创建. 节点为:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("创建节点 [{}] 失败", path);
        }
    }

    /**
     * 获取节点下的子节点.
     *
     * @param zkClient       {@link CuratorFramework}
     * @param rpcServiceName RpcServiceName
     * @return {@link List<String>}
     * @author GreyFable
     * @since 2024/8/28 14:21
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            log.error("获取节点 [{}] 失败", servicePath);
        }
        return result;
    }


    /**
     * 监听节点变化.
     *
     * @param rpcServiceName RpcServiceName
     * @param zkClient       {@link CuratorFramework}
     * @author GreyFable
     * @since 2024/8/28 14:23
     */
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) {
        String servicePath = "/" + rpcServiceName;
        CuratorCache curatorCache = CuratorCache.build(zkClient, servicePath);

        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forAll((type, oldData, data) -> {
                    List<String> serviceAddresses;
                    try {
                        serviceAddresses = zkClient.getChildren().forPath(servicePath);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
                })
                .build();

        curatorCache.listenable().addListener(listener);
        curatorCache.start();
    }

    /**
     * 清空注册数据.
     *
     * @param zkClient
     * @param inetSocketAddress
     * @author GreyFable
     * @since 2024/8/29 14:52
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("服务器上所有已注册的服务都已清除:[{}]", REGISTERED_PATH_SET);
    }
}
