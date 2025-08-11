package com.platform.modules.listener;

import com.platform.modules.msg.service.MsgService;
import io.netty.channel.DefaultChannelId;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 集群模式
 * messageModel = MessageModel.CLUSTERING,
 * consumeMode = ConsumeMode.ORDERLY,
 */

/**
 * 广播模式
 * messageModel = MessageModel.BROADCASTING
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "${rocketmq.topic}",
        consumerGroup = "${rocketmq.group}",
        messageModel = MessageModel.BROADCASTING)
public class MessageRocketListener implements RocketMQListener<String> {

    /**
     * 解决问题
     * org.apache.rocketmq.remoting.exception.RemotingTimeoutException: invokeSync call timeout
     */
    static {
        DefaultChannelId.newInstance();
    }

    @Resource
    private MsgService msgService;

    @Override
    public void onMessage(String message) {
        // 发送消息
        msgService.onMessage(message);
    }

}
