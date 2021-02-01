## Nacos 安装

小编这里下载的是`nacos-1.4.1`的版本，下载地址 
- [https://github.com/alibaba/nacos/releases/download/1.4.1/nacos-server-1.4.1.tar.gz](https://github.com/alibaba/nacos/releases/download/1.4.1/nacos-server-1.4.1.tar.gzp)
- [https://github.com/alibaba/nacos/releases/download/1.4.1/nacos-server-1.4.1.zip](https://github.com/alibaba/nacos/releases/download/1.4.1/nacos-server-1.4.1.zip)

启动命令在`/bin/`目录下,这里有可能会有个小坑
- 提示：Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better!
需要我们修改启动命令`startup.sh`

```shell script
if not exist "写成自己的jdk环境变量\bin\java.exe" echo Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better! & EXIT /B 1
set "JAVA=写成自己的jdk环境变量\bin\java.exe"
```
- 启动后发现是集群启动，其实找不到数据库
    - windows 下使用 ` cmd startup.cmd -m standalone`启动，表示单机启动
    - Linuz 下使用 ` bash startup.sh-m standalone`启动，表示单机启动


