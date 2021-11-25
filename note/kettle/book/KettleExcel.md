## 序言
这是我们用 Kettle 实现的第一个 demo，也是很简单的；需求如下，输入一个Excel 输出一个Excel

首先我们随意创一个Excel ，示例如下

![在这里插入图片描述](https://img-blog.csdnimg.cn/d0ddb99222d140bd9f77eaa527e737f6.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_14,color_FFFFFF,t_70,g_se,x_16)

---
## 创建转换
上面我们已经做好了准备工作，来到Kettle 工作页面

![在这里插入图片描述](https://img-blog.csdnimg.cn/f87aba98faf34cb5ac3bc1ecac2bfb5b.png)

点击上方的添加，你会发现有两种类型，一种是转换，一种是作业，这次我门用的是转换

![在这里插入图片描述](https://img-blog.csdnimg.cn/7c100b47602f4afba767ca87e9de6d22.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

---

## 输入逻辑
### 导入文件
双击我们刚刚创建的 Excel输入，得到结果如下

![在这里插入图片描述](https://img-blog.csdnimg.cn/19d3c70ecc79447ca10007ab11b5d28b.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

- 步骤名称随意写
- 表格类型选中POI的，当然您也可以选择其他的
- 在点击预览，找到我们之前准备好的Excel文件
- 点击增加
---

### 获取文件字段
我们切换页签来到，来到字段页面，点击最下方的获取头部信息即可，最后点击确定有，推出即可

![在这里插入图片描述](https://img-blog.csdnimg.cn/c9b7b86f69e847ac8f5444ed9adf198a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

---

## 输出逻辑
### 输出Excel

![在这里插入图片描述](https://img-blog.csdnimg.cn/4cb04af5f0d5426a8152dd3c6f5d6b0c.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

- 名称随意
- 扩展名按需即可

---
### 获取映射
切换页签到字段

![在这里插入图片描述](https://img-blog.csdnimg.cn/e56b2e534c004b11af80913900efc0f9.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

这里需要注意一下，这里的格式需要更改一下，否则导出后会有小数点

## 运行
点击红色区域按钮进运行即可，就会将文件输出到刚刚你指定的文件路径下

![在这里插入图片描述](https://img-blog.csdnimg.cn/e8145f390a8148e08e87e2ee52819348.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_18,color_FFFFFF,t_70,g_se,x_16)

当然您也可以查看控制台进行日志的查看

![在这里插入图片描述](https://img-blog.csdnimg.cn/0636dd124f554fcea29bc3d55b2ec443.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

**如果对您有所帮助，记得给小编点个赞哦，也可以店家小编的微信，372787553，进入程序猿/媛交流群！**