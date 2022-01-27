# Elasticsearch 安装

本文采用的是docker方式的安装，所以首先要确保你的机器上案由docker，如果您不了解docker的安装可以参考[Docker安装与卸载详细介绍](https://blog.csdn.net/weixin_38937840/article/details/104428747)

---

## 创建持久化卷

为防止重启带来的数据丢失，我们需要将 Elasticsearch 进行持久化存储，位置您可以根据您分区磁盘的大小而定

如在opt文件夹下创建如下文件

```
elasticsearch/config/
elasticsearch/data/
elasticsearch/plugins/
```

并在config目录下创建 `elasticsearch.yml ` 配置如下

```
network.bind_host: 0.0.0.0  #外网可访问
http.host: 0.0.0.0
http.cors.enabled: true
http.cors.allow-origin: "*"
# xpack.security.enabled: true # 这条配置表示开启xpack认证机制 spring boot连接使用
xpack.security.transport.ssl.enabled: true
xpack.security.enabled: false
```

----

## 创建网路连接

这是一个可选的步骤

```
 docker network create -d es-net
```

---

## 启动Elasticsearch

小编这里选择的是 `elasticsearch:7.8.0`这个版本，如果你需要其他的版本，将其改成指定的版本即可

```
docker run -p 9200:9200 -p 9300:9300 --name elasticsearch \
-e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
-e  "discovery.type=single-node" \
-v /opt/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /opt/elasticsearch/data:/usr/share/elasticsearch/data \
-v /opt/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
--network es-net \
-d --privileged=true elasticsearch:7.8.0
```

说明

> -p:端口映射
>
> -e discovery.type=single-node 单点模式启动
>
> -e ES_JAVA_OPTS="-Xms84m -Xmx512m"：设置启动占用的内存范围（实验环境启动后可能因为云服务器内存过小而占满）
> -v 目录挂载
> -d 后台运行

启动后您可以在浏览器输入您的ip:9200即可，出现如下的消息，表示启动成功

```
  "name" : "7cf5756f44f4",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "SFL5nHQJQz2F3ZwxM_R1mw",
  "version" : {
    "number" : "7.8.0",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "757314695644ea9a1dc2fecd26d1a43856725e65",
    "build_date" : "2020-06-14T19:35:50.234439Z",
    "build_snapshot" : false,
    "lucene_version" : "8.5.1",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

如果您启动后无法访问，可以通过 docker logs 您的容器id,查询启动是的信息