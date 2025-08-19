# 基础镜像使用java
FROM openjdk:8-jdk-alpine
# 作者
LABEL org.opencontainers.image.authors="admin admin@baidu.com"
# 最后更新时间
ENV REFRESHED_AT=2020-06-18
# 允许指定端口转发
EXPOSE 8888
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
VOLUME /tmp
# 将jar包添加到容器中并更名为impush.jar
WORKDIR /data
RUN mkdir logs
#ADD target/alpaca-push.jar impush.jar
ADD alpaca-push.jar impush.jar
# 修改时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 图片报错问题
ENV LANG=en_US.UTF-8
RUN echo -e "https://mirrors.ustc.edu.cn/alpine/v3.3/main" > /etc/apk/repositories \
    && echo "https://mirrors.ustc.edu.cn/alpine/v3.3/community" >> /etc/apk/repositories \
    && apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/*
# 运行jar包
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/data/impush.jar","--spring.config.location=file:/config/application-dev.yml"]