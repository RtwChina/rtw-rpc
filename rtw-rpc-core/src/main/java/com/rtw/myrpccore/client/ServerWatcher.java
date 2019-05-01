package com.rtw.myrpccore.client;

import com.rtw.myrpccore.factory.ZookeeperFactory;
import io.netty.channel.ChannelFuture;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

/**
 * @author rtw
 * @since 2019-05-01
 */
@Slf4j
public class ServerWatcher implements CuratorWatcher {
    @Override
    public void process(WatchedEvent event) throws Exception {
        try {
            log.info("触发监听ZK服务器节点变化");
            CuratorFramework curatorClient = ZookeeperFactory.create();
            String path = event.getPath();
            curatorClient.getChildren().usingWatcher(this);
            List<String> serverPaths = curatorClient.getChildren().forPath(path);
            NettyClient.realServerPath.clear();
            for(String serverPath : serverPaths) {
                NettyClient.realServerPath.add(serverPath.split("#")[0]);
                // 因为ZK的Netty IP已经改变了
                // 新创建一个ChannelFuture
                ChannelFuture channelFuture  = NettyClient.b.connect(serverPath.split("#")[0], 1024).sync(); // (5)
                ChannelManager.addFuture(channelFuture);
            }



        } catch (Exception e) {
            log.error("监听ZK服务器节点变化处理失败，", e);
        }
    }
}
