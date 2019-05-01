package com.rtw.myrpccore.start;

import com.rtw.myrpccore.start.param.StartUpParam;
import com.rtw.myrpccore.server.NettyServer;
import lombok.extern.slf4j.Slf4j;

/**
 * my-rpc启动类_RPC接口提供者
 * @author rtw
 * @since 2019-04-29
 */
@Slf4j
public class RpcProvideStartUp {
    // RPC提供类启动类
    public static synchronized void start(StartUpParam startUpParam) {
        log.info("启动 NettyServer");
        new Thread(() -> {
            NettyServer nettyServer = NettyServer.newInstance();
            nettyServer.init(startUpParam);
            nettyServer.beginServer();
        }).start();
    }

    public static Boolean finishStart() {
        NettyServer nettyServer = NettyServer.newInstance();
        return nettyServer.getNettyServerFlag().get();
    }
}
