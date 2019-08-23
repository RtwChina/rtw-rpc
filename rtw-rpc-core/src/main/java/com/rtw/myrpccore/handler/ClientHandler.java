package com.rtw.myrpccore.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import com.rtw.myrpccore.client.DefaultFuture;
import com.rtw.myrpccore.util.Response;

/**
 * @author rtw
 * @since 2019/2/24
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * Object msg 已经经过String转换过了
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 1. 先处理心跳信息
        if (msg.toString().equals("ping")) {
            ctx.channel().writeAndFlush("ping\r\n");
            log.info("Client 收到ping, 返回ping");
            return;
        }
        // 2. 将返回对象从JSON解码为Response
        Response response = null;
        try {
            response = JSONObject.parseObject(msg.toString(), Response.class);
        } catch (Exception e) {
            log.info("Client收到 请求 异常 msg={}", JSONObject.toJSONString(msg));
        }
        log.info("Client收到 请求 response={}", JSONObject.toJSONString(response));
        // 3. 唤醒客户端线程
        DefaultFuture.receive(response);
    }

    /**
     *
     * @param ctx
     * @param evt
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
