package com.platform.modules.push.initializer;

import com.platform.modules.push.handler.PushDataHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private PushDataHandler pushDataHandler;

    @Override
    protected void initChannel(SocketChannel sc) {
        // 设置log监听器，并且日志级别
        sc.pipeline().addLast("logging", new LoggingHandler("INFO"));
        // http 协议编解码器
        sc.pipeline().addLast("http-codec", new HttpServerCodec());
        // 大数据的分区传输
        sc.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        // http 协议编解码器
        sc.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        // 自定义消息处理器
        sc.pipeline().addLast("handler", pushDataHandler);
    }

}
