package com.rtw.myrpcconsumer;

import com.rtw.myrpccore.start.RpcConsumerStartUp;
import com.rtw.myrpccore.start.param.StartUpParam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.rtw.myrpccore", "com.rtw.myrpcconsumer"})
public class MyRpcConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyRpcConsumerApplication.class, args);


    }

}
