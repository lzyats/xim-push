package com.platform.modules.msg.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.platform.modules.msg.enums.ChannelEnum;
import com.platform.modules.msg.service.MsgService;
import com.platform.modules.msg.vo.MessageVo;
import com.platform.modules.push.handler.PushMsgHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 消息 服务层实现
 * </p>
 */
@Slf4j
@Service("msgService")
public class MsgServiceImpl implements MsgService {

    @Autowired
    private PushMsgHandler pushMsgHandler;

    @Override
    public void onMessage(String message) {
        // 验证消息
        if (!JSONUtil.isTypeJSON(message)) {
            return;
        }
        // 解析
        MessageVo messageVo = JSONUtil.toBean(message, MessageVo.class);
        // 异步执行
        ThreadUtil.execAsync(() -> {
            // 渠道
            ChannelEnum channelEnum = messageVo.getChannelEnum();
            // 消息内容
            String content = messageVo.getContent();
            //log.info("收到消息：{}",content);
            // 接收人员
            List<String> receiveList = messageVo.getReceiveList();
            switch (channelEnum) {
                case MSG:
                case SCAN:
                    receiveList.forEach(receiveId -> {
                        pushMsgHandler.sendMsg(receiveId, content);
                    });
                    break;
                case ALL:
                    pushMsgHandler.sendGroup(content);
                    break;
            }
        });
    }

}


