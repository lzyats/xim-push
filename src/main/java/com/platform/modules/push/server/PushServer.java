package com.platform.modules.push.server;

import com.platform.modules.push.initializer.PushInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动类
 */
@Slf4j
@Component
public class PushServer implements ApplicationRunner {

    @Autowired
    private PushInitializer pushInitializer;

    @Value("${platform.port}")
    private Integer port;

    /**
     * 开启websocket服务
     */
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(pushInitializer);
            Channel channel = bootstrap.bind(port).sync().channel();
            log.info("WebSocket启动成功：" + channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("运行出错：" + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        // 启动netty
        this.startServer();
    }

}
