package com.rtw.myrpccore.client;

import com.alibaba.fastjson.JSONObject;
import com.rtw.myrpccore.constant.Constants;
import com.rtw.myrpccore.factory.ZookeeperFactory;
import com.rtw.myrpccore.start.param.StartUpParam;
import com.rtw.myrpccore.util.ResponseCode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.rtw.myrpccore.handler.ClientHandler;
import com.rtw.myrpccore.util.Response;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

/**
 * 单例模式，启动本地Netty服务
 * @author rtw
 * @since 2019-04-14
 */
@Slf4j
@Data
public class NettyClient {

    public static final Bootstrap b = new Bootstrap();
    //默认连接服务端IP地址
    private String host = "localhost";
    //默认连接服务器端口
    private int port = 1024;
    // client是否可用标记位
    private AtomicBoolean nettyClientFlag = new AtomicBoolean(false);
    // 当前ZK上有效的nettyServer的IP地址
    public static Set<String> realServerPath = new HashSet<>();

    private ChannelManager channelManager;

    public void beginClient() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.handler(new ChannelInitializer<SocketChannel>() {// (4)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new ClientHandler());
                    ch.pipeline().addLast(new StringEncoder());
                }
            });

            CuratorFramework curatorClient = ZookeeperFactory.create();

            try {
                List<String> serverPaths = curatorClient.getChildren().forPath(Constants.SERVER_PATH);

                // 加上ZK监听服务器变化
                CuratorWatcher watcher = new ServerWatcher();
                curatorClient.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);
                for (String serverPath : serverPaths) {
                    realServerPath.add(serverPath.split("#")[0]);
                }
                if(realServerPath.size() > 0) {
                    host = realServerPath.toArray()[0].toString();
                }

            } catch (Exception e) {
                log.error("Zookeeper节点获取失败", e);
            }

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)
            ChannelManager.addFuture(f);
            nettyClientFlag.set(true);
        } catch (Exception e) {
            nettyClientFlag.set(false);
            log.error("Client启动异常", e);
        } finally {
//            workerGroup.shutdownGracefully();
        }
    }

    // 发送请求。
    // 1。每一个请求都是用同一个连接，需要注意并发
    public Response send(ClientRequest request) {
        if (!nettyClientFlag.get()) {
            log.error("当前NettyClient未启动成功");
            return Response.FailResponse(request.getId(), ResponseCode.FAIL, "当前NettyClient未启动成功");
        }

        // 随机获取一个ChannelFuture
        ChannelFuture channelFuture = ChannelManager.getByRandom();
        channelFuture.channel().writeAndFlush(JSONObject.toJSONString(request));
        channelFuture.channel().writeAndFlush("\r\n");
        DefaultFuture df = new DefaultFuture(request);
        return df.get(1000000);
    }



    // 初始化NettyServer
    public void init(StartUpParam startUpParam) {
        this.host = startUpParam.getIp();
        this.port = startUpParam.getPort();
    }

    private static NettyClient nettyClient = new NettyClient();

    public static NettyClient newInstance() {
        return nettyClient;
    }
    private NettyClient() {
    }
}
