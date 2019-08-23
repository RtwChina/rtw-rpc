package com.rtw.myrpccore.server;

import com.rtw.myrpccore.constant.Constants;
import com.rtw.myrpccore.factory.ZookeeperFactory;
import com.rtw.myrpccore.start.param.StartUpParam;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.rtw.myrpccore.handler.ServerHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;

/**
 * 单例模式，启动本地Netty服务
 * @author rtw
 * @since 2018/11/9
 */
@Slf4j
@Component
@Data
public class NettyServer {

    // server绑定端口号 默认 1024
    private int port = 1024;

    // server是否可用标记位
    private AtomicBoolean nettyServerFlag = new AtomicBoolean(false);

    // boss线程池
    EventLoopGroup bossGroup = new NioEventLoopGroup(); //1

    // 工作线程池
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void beginServer() {

        try {
            ServerBootstrap b = new ServerBootstrap(); //2
            b.group(bossGroup, workerGroup)
                    // 队列等待数量
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 心跳包
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class) //3
                    .childHandler(new ChannelInitializer<SocketChannel>() { //4
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 分隔符解码: Delimiters.lineDelimiter()[0] ==  '\r', '\n'
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()[0]));
                            // 字符串解码
                            ch.pipeline().addLast(new StringDecoder());
                            // 心跳
                            ch.pipeline().addLast(new IdleStateHandler(100, 100, 100, TimeUnit.SECONDS));
                            // 业务处理
                            ch.pipeline().addLast(new ServerHandler());
                            // 字符串编码
                            ch.pipeline().addLast(new StringEncoder());

                        }
                    });
            ChannelFuture f = b.bind(port).sync(); //5
            CuratorFramework client = ZookeeperFactory.create();
            InetAddress inetAddress = InetAddress.getLocalHost();
            // 去Zookeper注册一个临时的文件夹，文件名为当前server地址,当server与Zk的回话结束时，该节点就会自己消失。
            // EPHEMERAL_SEQUENTIAL表示在创建节点的时候会在末尾增加一个单调递增的数字。主要是为了防止客户端掉线后ZK反映比较慢，然后客户端上线后就会有重复节点的问题
            client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(Constants.SERVER_PATH + "/" + inetAddress.getHostAddress() + "#");
            log.info("nettyServer 本地IP地址={}，开始监听{}端口", inetAddress.getHostAddress(), port);
            nettyServerFlag.set(true);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            nettyServerFlag.set(false);
            log.error("nettyServer启动异常", e);
        } finally {
            nettyServerFlag.set(false);
            log.info("开始关闭bossGroup、workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    // 初始化NettyServer
    public void init(StartUpParam startUpParam) {
        this.port = startUpParam.getPort();
    }

    // 关闭Netty服务器
    public void shortDown() {
        nettyServerFlag.set(false);
        log.info("开始关闭bossGroup、workerGroup");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private static NettyServer nettyServer = new NettyServer();

    public static NettyServer newInstance() {
        return nettyServer;
    }
    private NettyServer() {
    }
 }
