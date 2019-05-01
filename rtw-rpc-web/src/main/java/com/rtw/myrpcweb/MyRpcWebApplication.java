package com.rtw.myrpcweb;

import com.rtw.myrpccore.constant.Constants;
import com.rtw.myrpccore.factory.ZookeeperFactory;
import com.rtw.myrpccore.start.RpcConsumerStartUp;
import com.rtw.myrpccore.start.RpcProvideStartUp;
import com.rtw.myrpccore.start.param.StartUpParam;
import java.net.InetAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.rtw.myrpccore", "com.rtw.myrpcweb"})
@Slf4j
public class MyRpcWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyRpcWebApplication.class, args);

        // 启动Rpc Provide
        StartUpParam startUpParam = new StartUpParam();
        startUpParam.setPort(1024);
        RpcProvideStartUp.start(startUpParam);
    }
}
