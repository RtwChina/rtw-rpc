package com.rtw.myrpccore.start;

import com.rtw.myrpccore.client.NettyClient;
import com.rtw.myrpccore.server.NettyServer;
import com.rtw.myrpccore.start.param.StartUpParam;
import lombok.extern.slf4j.Slf4j;

/**
 *  my-rpc启动类_RPC接口消费者
 * @author rtw
 * @since 2019-04-29
 */
@Slf4j
public class RpcConsumerStartUp {
    // RPC提供类启动类
    public static synchronized void start(StartUpParam startUpParam) {
        log.info("启动 NettyClient");
        Thread thread = new Thread(() -> {
            NettyClient nettyClient = NettyClient.newInstance();
            nettyClient.init(startUpParam);
            nettyClient.beginClient();
        });
        thread.start();
        try {
            // 第一次启动Client需要休眠1S
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("睡眠时被打断，", e);
        }
    }
}
