package com.platform;

import cn.hutool.cron.CronUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动程序
 */
@Slf4j
@SpringBootApplication
public class AppStartUp implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(AppStartUp.class, args);
        log.info("(♥◠‿◠)ﾉﾞ  启动成功   ლ(´ڡ`ლ)ﾞ");
    }

    @Override
    public void run(ApplicationArguments args) {
        // 定时任务开启
        CronUtil.setMatchSecond(true);
        CronUtil.start(true);
    }
}
