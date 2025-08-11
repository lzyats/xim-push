package com.platform.modules.msg.service;

/**
 * <p>
 * 消息 服务层
 * </p>
 */
public interface MsgService {

    /**
     * 发送消息
     */
    void onMessage(String message);

}
