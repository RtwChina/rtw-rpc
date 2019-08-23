package com.rtw.myrpccore.client;

import com.rtw.myrpccore.util.Response;
import com.rtw.myrpccore.util.ResponseCode;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rtw
 * @since 2019-05-08
 */
@Slf4j
@Data
public class DefaultFutureManager {
    // 当前客户端测所有的DefaultFuture
    private ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = new ConcurrentHashMap<>();


    // 接收到返回值，需要删除Future，唤醒业务等待线程
    public static void receive(Response response) {
        DefaultFuture defaultFuture = DefaultFutureManager.newInstance().getFuture(response.getId());
        if (defaultFuture != null) {
            try {
                defaultFuture.setResponse(response);
                // 唤醒Default的业务线程
                defaultFuture.getCountDownLatch().countDown();
                // 在FutureManager中删除相对应的Future
                DefaultFutureManager.newInstance().removeFuture(response.getId());
            } catch (Exception e) {
                log.error("返回值处理异常", e);
            }
        }
    }

    // 根据响应id获取对应的Future
    public DefaultFuture getFuture(Long responseId) {
        return this.allDefaultFuture.get(responseId);
    }

    // 删除对应响应id的Future
    public void removeFuture(Long responseId) {
        this.allDefaultFuture.remove(responseId);
    }

    // 获
    public Set<Long> getIds() {
        return this.allDefaultFuture.keySet();
    }
    static class FutureThread extends Thread {
        @Override
        public void run() {
            Set<Long> ids = DefaultFutureManager.newInstance().getIds();
            for (Long id : ids) {
                DefaultFuture df = DefaultFutureManager.newInstance().getFuture(id);
                if (df == null) {
                    DefaultFutureManager.newInstance().removeFuture(id);
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
        DefaultFutureManager.FutureThread futureThread = new DefaultFutureManager.FutureThread();
        futureThread.setDaemon(true); // 设置为后台守护线程
        futureThread.start();
    }

    private static DefaultFutureManager defaultFutureManager = new DefaultFutureManager();
    private DefaultFutureManager() {
    }
    public static DefaultFutureManager newInstance() {
        return defaultFutureManager;
    }

}
