package com.platform.modules.push.handler;

import com.platform.modules.msg.enums.ChannelEnum;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker13;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自定义处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class PushDataHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker13 serverHandshaker;

    @Autowired
    private PushMsgHandler pushMsgHandler;

    @Autowired
    private PushAuthHandler pushAuthHandler;

    @Override
    public void channelRead0(ChannelHandlerContext context, Object object) {
        // 处理http请求
        if (object instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) object;
            // 获取ChannelEnum
            ChannelEnum channelEnum = pushAuthHandler.getChannelEnum(request);
            // 获取Channel
            Channel channel = context.channel();
            if (channelEnum == null) {
                channel.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED));
                channel.close();
                return;
            }
            // 获取Token
            String token = pushAuthHandler.getToken(request, channelEnum);
            // 握手处理
            if (serverHandshaker == null) {
                // 注意，这条地址别被误导了，其实这里填写什么都无所谓，WS协议消息的接收不受这里控制
                String url = "ws://127.0.0.1:1234/websocket";
                serverHandshaker = new WebSocketServerHandshaker13(url, null, false, Integer.MAX_VALUE);
            }
            serverHandshaker.handshake(channel, request);
            // 加入通道
            pushMsgHandler.addChannel(token, channel, channelEnum);
        }
        // WebSocket处理
        else if (object instanceof TextWebSocketFrame) {
            Channel channel = context.channel();
            TextWebSocketFrame request = (TextWebSocketFrame) object;
            String content = request.text();
            this.doSocketRequest(channel, content);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
        // 注册连接
        Channel channel = context.channel();
        log.info("建立：" + channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        Channel channel = context.channel();
        // 断开连接
        log.info("断开：" + channel);
        // 移除当前通道
        context.pipeline().remove(this);
        // 关闭连接
        pushMsgHandler.removeChannel(channel);
        context.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        // 异常连接
        Channel channel = context.channel();
        log.info("异常：{}，{}", channel, pushMsgHandler.getChannelToken(channel));
        // 移除当前通道
        context.pipeline().remove(this);
        // 关闭连接
        pushMsgHandler.removeChannel(channel);
        context.close();
    }

    /**
     * 处理socket请求
     */
    private void doSocketRequest(Channel channel, String content) {
        // ping
        if ("ping".equals(content)) {
            // pong
            pushMsgHandler.pong(channel);
        }
    }

}