## 概述
阅读本文前，您如果还不了解Kettle如何连接Oracle，可以参考 [Kettle 连接 oracle 无坑版本](https://blog.csdn.net/weixin_38937840/article/details/121498960)

## 创建表输入
在这之前我们需要先创建一个转换，如果您还不了解可以参考[Kettle Excel 输入输出 的创建方式](https://blog.csdn.net/weixin_38937840/article/details/121521598)
![在这里插入图片描述](https://img-blog.csdnimg.cn/e14f5f63679c49bf86d6f5c62669b2a0.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

- 如果您没有建立数据库连接，在这里新建即可，如果已经建立则可以通过编辑进行选择
- 选择完数据库连接后，点击获取SQL查询语句，现在你需要同步的表即可

---

## 同步到PGSQL中
### 创建一个 插入更新的输出
![在这里插入图片描述](https://img-blog.csdnimg.cn/be6fe435ba9c4a7295dc510de3e10b80.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

- 数据连着根据上面的操作一样，只不过Pgsql需要选择模式，选择你对应数据库的模式即可
- 目标表
	- 可以通过预览选择要同步的数据
	- 可以自己随便起一个名字，然后点击下面2黄色框的SQL，就会弹出3的对话框，包含了创建语句，点击执行即可

---

### 编辑映射
![在这里插入图片描述](https://img-blog.csdnimg.cn/a9a9a1493290407a87d81aaabc9a23c5.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)

- 通过红框的获取字段来获取更新的条件，这里可以按照需求进行选择
- 通过黄框获取和更新字段连获取表的全集，也可以按需进行

最后的最后，进行保存，点击运行即可

**如果对您有所帮助，记得给小编点个赞哦，也可以店家小编的微信，372787553，进入程序猿/媛交流群！**