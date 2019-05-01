package com.rtw.myrpccore.factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author rtw
 * @since 2019-04-30
 */
public class ZookeeperFactory {
    public static CuratorFramework client;

    public static CuratorFramework create() {
        if (client == null) {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
            client.start();
        }
        return client;
    }

    public static void main(String[] args) throws Exception{
        CuratorFramework client = create();
        // 去zookeeper注册节点
        client.create().forPath("/netty");
    }
}
