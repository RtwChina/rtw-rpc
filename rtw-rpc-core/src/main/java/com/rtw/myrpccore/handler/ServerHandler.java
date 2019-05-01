package com.rtw.myrpccore.handler;

import com.alibaba.fastjson.JSONObject;
import com.rtw.myrpccore.util.ResponseCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import com.rtw.myrpccore.medium.Media;
import com.rtw.myrpccore.server.ServerRequest;
import com.rtw.myrpccore.util.Response;

/**
 * @author rtw
 * @since 2019/2/14
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 1. 拿到服务请求对象 ServerRequest
        ServerRequest serverRequest = JSONObject.parseObject(msg.toString(), ServerRequest.class);
        log.info("Server收到 请求 serverRequest={}", JSONObject.toJSONString(serverRequest));
        // 2. 解析服务请求对象 并 调用业务代码。
        Object result = Media.newInstance().process(serverRequest);
        // 3. 将业务返回值存在 Response 返回
        Response response = Response.SuccessResponse(serverRequest.getId(), ResponseCode.SUCCESS, JSONObject.toJSON(result));
        ctx.channel().writeAndFlush(JSONObject.toJSONString(response) + "\r\n");
    }

    /**
     * 当有Event被触发时会调用
     * @param ctx
     * @param evt
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("读空闲===");
                ctx.channel().close();
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("写空闲===");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("都空闲===");
                ctx.channel().writeAndFlush("ping\r\n");
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
