package com.rtw.myrpccore.client;

import com.alibaba.fastjson.JSON;
import com.rtw.myrpccore.util.ResponseCode;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.rtw.myrpccore.util.Response;

/**
 * 管理客户端的业务线程，将服务响应与服务请求对应起来。
 *
 * @author rtw
 * @since 2019-04-14
 */
@Data
@Slf4j
public class DefaultFuture {

//    public static ConcurrentHashMap<Long, DefaultFuture> allDeafaultFuture = new ConcurrentHashMap<>();
//    private final Lock lock = new ReentrantLock();

    //    private Condition condition = lock.newCondition();
    private Response response;

    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private long timeOut = 2 * 60 * 1000;
    private long startTime = System.currentTimeMillis();


    // 主线程获取数据，等待结果
    public Response get(long time) {
        try {
            // 死循环，直到：1. countDownLatch被唤醒 且 可以拿到返回值response
            while (!done()) {
                countDownLatch.await(time, TimeUnit.MILLISECONDS);
                if ((System.currentTimeMillis() - startTime) > time) {
                    log.error("请求超时 Response = {}", JSON.toJSONString(response));
                    break;
                }
            }
        } catch (InterruptedException e) {
            log.error("中断", e);
        }
        return this.response;
    }

    // 判断是否可以拿到返回值，即服务端的响应
    private boolean done() {
        if (this.response != null) {
            return true;
        }
        return false;
    }
}

















