package com.rtw.myrpcconsumer;

import com.alibaba.fastjson.JSON;
import com.rtw.myrpccore.annotation.RemoteInvoking;
import com.rtw.myrpccore.start.RpcConsumerStartUp;
import com.rtw.myrpccore.start.param.StartUpParam;
import com.rtw.myrpcprovideapi.TestDubboProvide;
import com.rtw.myrpcprovideapi.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MyRpcConsumerApplicationTests {

    @RemoteInvoking
    private TestDubboProvide testDubboProvide;
    @Test
    public void contextLoads() {

        StartUpParam startUpParamClient = new StartUpParam("localHost", 1024);
        RpcConsumerStartUp.start(startUpParamClient);

        User user = new User(26, "rtw");
        Object str = testDubboProvide.getUserName(user);
        log.info("调用远程提供方成功，返回为={}", JSON.toJSONString(str));
    }

}
