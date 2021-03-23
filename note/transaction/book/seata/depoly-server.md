# 部署 Server

Server支持多种方式部署：直接部署，使用 Docker, 使用 Docker-Compose, 使用 Kubernetes, 使用 Helm.

## 直接部署

1. 在[RELEASE](https://github.com/seata/seata/releases) 页面下载相应版本并解压
2. 直接启动

在 Linux/Mac 下

```bash
$ sh ./bin/seata-server.sh
```

在 Windows 下

```cmd
bin\seata-server.bat
```

### 支持的启动参数

| 参数 | 全写         | 作用                       | 备注                                                         |
| :--- | :----------- | :------------------------- | :----------------------------------------------------------- |
| -h   | --host       | 指定在注册中心注册的 IP    | 不指定时获取当前的 IP，外部访问部署在云环境和容器中的 server 建议指定 |
| -p   | --port       | 指定 server 启动的端口     | 默认为 8091                                                  |
| -m   | --storeMode  | 事务日志存储方式           | 支持`file`,`db`,`redis`，默认为 `file` 注:redis需seata-server 1.3版本及以上 |
| -n   | --serverNode | 用于指定seata-server节点ID | 如 `1`,`2`,`3`..., 默认为 `1`                                |
| -e   | --seataEnv   | 指定 seata-server 运行环境 | 如 `dev`, `test` 等, 服务启动时会使用 `registry-dev.conf` 这样的配置 |

如：

```bash
$ sh ./bin/seata-server.sh -p 8091 -h 127.0.0.1 -m file
```

