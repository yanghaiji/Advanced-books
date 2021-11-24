## Kettle 安装与优化

kettle是一个ETL（Extract, Transform and Load抽取、转换、载入）工具，ETL工具在数据仓库项目使用非常频繁，kettle也可以应用在以下一些场景：

- 在不同应用或数据库之间整合数据
 - 把数据库中的数据导出到文本文件
  - 大批量数据装载入数据库
  - 数据清洗
  - 集成应用相关项目是个使用

kettle使用非常简单，通过图形界面设计实现做什么业务，无需写代码去实现，因此,kettle是以面向元数据来设计；

kettle支持很多种输入和输出格式，包括文本文件，数据表，以及商业和免费的数据库引擎。另外，kettle强大的转换功能让您非常方便操纵数据。

## 安装kettle 

-  下载编译好的安装包
   [下载地址 https://sourceforge.net/projects/pentaho/files/Data%20Integration/](https://sourceforge.net/projects/pentaho/files/Data%20Integration/)  但是这个地址在过完，下载特别的慢
-  自行编译源码
   [源码地址 https://github.com/pentaho/pentaho-kettle](https://github.com/pentaho/pentaho-kettle) 选择需要使用的版本，安装说明文档一步步执行即可，但是这里的坑很多
-  小编下载好的安装包
   链接:[https://pan.baidu.com/s/17d4flu-YJbBppFe4Zhq6dg](https://pan.baidu.com/s/17d4flu-YJbBppFe4Zhq6dg ) 提取码:8vl3 

安装完后，不要着急启动，首先检查系统的环境

- java 的版本，最好是 java8，其他版本可能会导致启动后卡死
  之前小编电脑安装了java12 导致启动kettle后直接卡死
- 如果无法启动，修改一下启动参数的jvm大小(Spoon.bat)

> if "%PENTAHO_DI_JAVA_OPTIONS%"=="" set PENTAHO_DI_JAVA_OPTIONS="-Xms512m" "-Xmx512m" "-XX:MaxPermSize=256m"

启动后，还是需要等待一会的，之后就可以进行操作了
![在这里插入图片描述](https://img-blog.csdnimg.cn/9603d6d298d84820b4f66b6c54c15ba4.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

