package com.platform.modules.push.handler;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.platform.modules.msg.enums.ChannelEnum;
import com.platform.modules.msg.utils.EncryptUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Map;

@Component
public class PushAuthHandler {

    /**
     * 获取通道
     */
    public ChannelEnum getChannelEnum(FullHttpRequest request) {
        String channelPath = ReUtil.getGroup0("/[a-z0-9]*", request.uri());
        if ("/ws".equals(channelPath)) {
            return ChannelEnum.MSG;
        }
        return ChannelEnum.SCAN;
    }

    /**
     * 获取Token
     */
    public String getToken(FullHttpRequest request, ChannelEnum channelEnum) {
        // 转换请求
        Map<String, String> requestMap = HttpUtil.decodeParamMap(request.uri(), Charset.defaultCharset());
        String token = requestMap.get("Authorization");
        // 解密
        if (!ChannelEnum.SCAN.equals(channelEnum)) {
            token = decrypt(token);
        }
        // 如果空，则给一个伪token
        if (StrUtil.isEmpty(token)) {
            token = RandomUtil.randomString(64);
        }
        return token;
    }

    @Value("${platform.secret:e3dc7597a259bd3a}")
    private String secret;

    /**
     * 解密
     */
    private String decrypt(String token) {
        try {
            String decrypt = EncryptUtils.decrypt(token, secret);
            JSONObject jsonObject = JSONUtil.parseObj(decrypt);
            return jsonObject.getStr("userId");
        } catch (Exception e) {
            Console.log(e);
            return null;
        }
    }

}
