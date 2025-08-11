package com.platform.modules.msg.vo;

import com.platform.modules.msg.enums.ChannelEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true) // 链式调用
@NoArgsConstructor
public class MessageVo {

    /**
     * 接收渠道
     */
    private String channel;
    /**
     * 接收人员
     */
    private List<String> receiveList;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 接收渠道
     */
    private ChannelEnum channelEnum;

    public ChannelEnum getChannelEnum() {
        for (ChannelEnum channelEnum : ChannelEnum.values()) {
            if (channelEnum.getCode().equalsIgnoreCase(channel)) {
                return channelEnum;
            }
        }
        return ChannelEnum.SCAN;
    }

}
