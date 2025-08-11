package com.platform.modules.push.handler;

import com.platform.modules.msg.enums.ChannelEnum;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PushMsgHandler {

    // GROUP
    private static ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // CHANNEL
    private static Map<String, ChannelGroup> CHANNEL_MAP = new ConcurrentHashMap<>();
    // TOKEN
    private static Map<Channel, String> CHANNEL_TOKEN = new ConcurrentHashMap<>();

    /**
     * 增加channel
     */
    public void addChannel(String token, Channel channel, ChannelEnum channelEnum) {
        ChannelGroup channelGroup = CHANNEL_MAP.get(token);
        if (channelGroup == null) {
            channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            CHANNEL_MAP.put(token, channelGroup);
        }
        channelGroup.add(channel);
        CHANNEL_TOKEN.put(channel, token);
        if (ChannelEnum.MSG.equals(channelEnum)) {
            CHANNEL_GROUP.add(channel);
        }
    }

    /**
     * 获取channelToken
     */
    public String getChannelToken(Channel channel) {
        if (channel == null) {
            return "-";
        }
        String channelToken = CHANNEL_TOKEN.get(channel);
        if (StringUtils.isEmpty(channelToken)) {
            channelToken = "-";
        }
        return channelToken;
    }

    /**
     * 移除当前channel
     */
    public void removeChannel(Channel channel) {
        if (channel == null) {
            return;
        }
        String channelToken = getChannelToken(channel);
        ChannelGroup channelGroup = CHANNEL_MAP.get(channelToken);
        if (channelGroup != null) {
            channelGroup.remove(channel);
        }
        CHANNEL_TOKEN.remove(channel);
        CHANNEL_GROUP.remove(channel);
        channel.close();
    }

    /**
     * pong
     */
    public void pong(Channel channel) {
        channel.writeAndFlush(new TextWebSocketFrame("pong"));
    }

    /**
     * 发送消息
     */
    public void sendMsg(String token, String content) {
        ChannelGroup channelGroup = CHANNEL_MAP.get(token);
        if (channelGroup != null) {
            channelGroup.writeAndFlush(new TextWebSocketFrame(content));
        }
    }

    /**
     * 发送Group消息
     */
    public void sendGroup(String content) {
        CHANNEL_GROUP.writeAndFlush(new TextWebSocketFrame(content));
    }

}
