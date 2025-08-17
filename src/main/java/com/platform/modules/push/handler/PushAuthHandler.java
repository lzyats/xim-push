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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Map;
@Slf4j
@Component
public class PushAuthHandler {

    @Value("${platform.secret}")
    private String secret;

    /**
     * 获取通道
     */
    public ChannelEnum getChannelEnum(FullHttpRequest request) {
        //String channelPath = ReUtil.getGroup0("/[a-z0-9]*", request.uri());
        String uri = request.uri();
        String path = uri.contains("?") ? uri.substring(0, uri.indexOf("?")) : uri;
        String channelPath = ReUtil.getGroup0("/[a-z0-9]*", path);
        if ("/ws".equals(channelPath)) {
            return ChannelEnum.MSG;
        }
        return ChannelEnum.SCAN;
    }

    /**
     * 获取Token
     */
    public String getToken(FullHttpRequest request, ChannelEnum channelEnum) {
        log.info("请求token");
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


    /**
     * 解密
     */
    private String decrypt(String token) {
        try {
            String decrypt = EncryptUtils.decrypt(token, secret);
            JSONObject jsonObject = JSONUtil.parseObj(decrypt);
            return jsonObject.getStr("userId");
        } catch (Exception e) {
            log.error("Token解密失败: {}", token, e);
            return null;
        }
    }

}
