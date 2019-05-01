package com.rtw.myrpccore.client;

import com.alibaba.fastjson.JSON;
import com.rtw.myrpccore.util.ResponseCode;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.rtw.myrpccore.util.Response;

/**
 * @author rtw
 * @since 2019-04-14
 */
@Data
@Slf4j
public class DefaultFuture {

    public static ConcurrentHashMap<Long, DefaultFuture> allDeafaultFuture = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Response response;
    private long timeOut = 2 * 60 * 1000;
    private long startTime = System.currentTimeMillis();

    public DefaultFuture(ClientRequest clientRequest) {
        allDeafaultFuture.put(clientRequest.getId(), this);
    }

    // 主线程获取数据，等待结果
    public Response get(long time) {
        lock.lock();
        try {
            while (!done()) {
                condition.await(time, TimeUnit.MILLISECONDS);
                if ((System.currentTimeMillis() - startTime) > time) {
                    log.error("请求超时 Response = {}", JSON.toJSONString(response));
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return this.response;
    }

    private boolean done() {
        if(this.response != null) {
            return true;
        }
        return false;
    }

    // 接收到返回值
    public static void receive(Response response) {
        DefaultFuture defaultFuture = allDeafaultFuture.get(response.getId());
        if (defaultFuture != null) {
            defaultFuture.getLock().lock();
            try {
                defaultFuture.setResponse(response);
                defaultFuture.condition.signalAll();
                allDeafaultFuture.remove(response.getId());
            } catch (Exception e) {
                log.error("返回值处理异常");
            } finally {
                defaultFuture.getLock().unlock();
            }
        }
    }
    static class FutureThread extends Thread {
        @Override
        public void run() {
            Set<Long> ids = allDeafaultFuture.keySet();
            for (Long id : ids) {
                DefaultFuture df = allDeafaultFuture.get(id);
                if (df == null) {
                    allDeafaultFuture.remove(df);
                } else {
                    // 如果链路超时了 直接receive异常，进行触发
                    if (df.getTimeOut() < (System.currentTimeMillis() - df.getStartTime())) {
                        Response response = Response.FailResponse(id, ResponseCode.TIMEOUT, "链路超时");
                        receive(response);
                    }
                }
            }
            super.run();
        }
    }

    static {
        log.info("初始化allDeafaultFuture的守护线程");
        FutureThread futureThread = new FutureThread();
        futureThread.setDaemon(true); // 设置为后台守护线程
        futureThread.start();
    }

}

















