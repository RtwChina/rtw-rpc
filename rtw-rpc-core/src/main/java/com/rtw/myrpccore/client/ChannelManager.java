package com.rtw.myrpccore.client;

import io.netty.channel.ChannelFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author rtw
 * @since 2019-05-01
 */
public class ChannelManager {

    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();

    // 循环遍历的ChannelFuture下标
    private static AtomicLong index = new AtomicLong(0);

    public static void removeFuture(ChannelFuture channel) {
        channelFutures.remove(channel);
    }
    public static void addFuture(ChannelFuture channel) {
        channelFutures.add(channel);
    }
    public static void clear(ChannelFuture channel) {
        channelFutures.clear();
    }

    public static ChannelFuture getByRandom() {

        if (index.intValue() >= channelFutures.size()) {
            // 当index的下标超过Future列表的话，则设置为0，从新开始
            index.set(0);
        }
        ChannelFuture channelFuture = channelFutures.get(index.intValue());
        if (channelFuture != null) {
            // 自增
            index.incrementAndGet();
            return channelFuture;
        } else {
            // 获取为空的话，表示出现多线程修改，那么在重试两次进行自增获取。
            ChannelFuture channelFutureAgain;
            int i = 0;
            do {
                i++;
                if (index.intValue() >= channelFutures.size()) {
                    // 当index的下标超过Future列表的话，则设置为0，从新开始
                    index.set(0);
                }
                channelFutureAgain = channelFutures.get(index.intValue());
                index.incrementAndGet();
                if (channelFutureAgain != null) {
                    return channelFutureAgain;
                }
            } while (i >= 2);
            return null;
        }

    }
}
