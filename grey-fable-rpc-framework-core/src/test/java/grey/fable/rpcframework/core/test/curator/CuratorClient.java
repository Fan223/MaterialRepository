package grey.fable.rpcframework.core.test.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Curator 客户端
 *
 * @author GreyFable
 * @since 2024/8/22 11:33
 */
public class CuratorClient {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                // 会话超时时间
                .sessionTimeoutMs(5000)
                // 连接超时时间
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();

        CuratorClient curatorClient = new CuratorClient();
        curatorClient.checkExists(client);
        curatorClient.create(client);
        curatorClient.get(client);
        curatorClient.getChildren(client);
        curatorClient.update(client);
        curatorClient.delete(client);
        curatorClient.listener(client);
    }

    private void checkExists(CuratorFramework client) throws Exception {
        Stat stat = client.checkExists().forPath("/node1/0001");
        if (null != stat) {
            System.out.println("节点已存在");
        }
    }

    private void create(CuratorFramework client) throws Exception {
        client.create().forPath("/node1/0001");
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT).forPath("/node1/0003", "0003".getBytes());
    }

    private byte[] get(CuratorFramework client) throws Exception {
        return client.getData().forPath("/node1/0001");
    }

    private List<String> getChildren(CuratorFramework client) throws Exception {
        return client.getChildren().forPath("/node1/0001");
    }

    private void update(CuratorFramework client) throws Exception {
        client.setData().forPath("/node1/0001", "0001".getBytes());
    }

    private void delete(CuratorFramework client) throws Exception {
        client.delete().deletingChildrenIfNeeded().forPath("/node1/0001");
    }

    private void listener(CuratorFramework client) {
        CuratorCache curatorCache = CuratorCache.build(client, "/node1");
        // CuratorCache curatorCache = CuratorCache.build(client, "/node1", CuratorCache.Options.SINGLE_NODE_CACHE);

        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forCreates(childData ->
                        System.out.println("create: " + childData.getPath() + " " + new String(childData.getData())))
                .forChanges((oldNode, node) -> System.out.println("change"))
                .forCreatesAndChanges((oldNode, node) -> System.out.println("createAndChange"))
                .forDeletes(childData -> System.out.println("delete"))
                .forAll((type, oldData, data) -> {
                    if (type.name().equals(CuratorCacheListener.Type.NODE_CREATED.name())) {
                        System.out.println("create");
                    } else if (type.name().equals(CuratorCacheListener.Type.NODE_CHANGED.name())) {
                        System.out.println("change");
                    } else if (type.name().equals(CuratorCacheListener.Type.NODE_DELETED.name())) {
                        System.out.println("delete");
                    }
                })
                .build();

        curatorCache.listenable().addListener(listener);
        curatorCache.start();
    }
}
