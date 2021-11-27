如果您不了解如何安装kettle 请参考[kettle 安装与调优 https://blog.csdn.net/weixin_38937840/article/details/121352284](https://blog.csdn.net/weixin_38937840/article/details/121352284)

---
## 连接前的准备
下载oracle连接驱动

orale 驱动下载[ https://www.oracle.com/cn/database/technologies/enterprise-edition/jdbc.html](https://www.oracle.com/cn/database/technologies/enterprise-edition/jdbc.html)
将下载的驱动放入到 kettel 安装目录里的lib文件夹下
![在这里插入图片描述](https://img-blog.csdnimg.cn/eeed0c23679441d0b97a845923326900.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_16,color_FFFFFF,t_70,g_se,x_16)
**如果您下载的kettle版本带有了oracle 的驱动最好先进行删除**

##  配置连接
- 创建一个转换
![在这里插入图片描述](https://img-blog.csdnimg.cn/5157d5f145714644a41fff50713629aa.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)
---
- 配置连接
![在这里插入图片描述](https://img-blog.csdnimg.cn/bf73d713aca949ab9e9b60d2173ad862.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)
**注意事项 ：数据库连接名称如果为服务名，请注意在前面加 /** 
---
- 在网上看到了另一种连接方式
![在这里插入图片描述](https://img-blog.csdnimg.cn/9450cb77eac8489a9c1fbaf373049c89.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)
> 主机名称：不用填
数据库名称：填写域名或者IP:端口/服务名
数据表空间：不用填
索引表空间：不用填
端口号：-1，因为再填写数据库名称时已经填写，所以这里填写-1
用户名：用户名
密码：密码
---
**添加小编微信，一起进去程序员交流群 ，微信: 372787553**