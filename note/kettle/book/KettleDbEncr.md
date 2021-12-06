## 导语
如果你在Java里调用创建好的ktr文件，在开发时还是可以用的，但是上了生产测试和生产就没办法使用了，数据连接换了，这时候你可能会想到，我在创建一个转换不就好了，但是这个是有风险的，Kettle还是很友好的，为我提供了加密解密的工具！

---
## Kettle工具加密
Kettle的客户端为我提供了加密的工具


- 进入kettle的安装目录

	 - windows系统命令行执行：Encr.bat -kettle 123

	 - linux/mac系统命令行执行：encr.sh -kettle 123
![在这里插入图片描述](https://img-blog.csdnimg.cn/f93dc44e5dfa44fba3149e2a301a10c0.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBASmF2Yeaciei0pw==,size_20,color_FFFFFF,t_70,g_se,x_16)
解密需要创建转换实现，相对麻烦些，可以直接使用下面的java代码实现
---
## Java代码实现加解密
### 加密

```bash
        KettleClientEnvironment.init();
        String dev = Encr.encryptPassword("123");
        System.out.println(Encr.PASSWORD_ENCRYPTED_PREFIX+dev);
```
`Encr.PASSWORD_ENCRYPTED_PREFIX` 您也可以不写，因为这是固定的

### 解密

```bash
        KettleClientEnvironment.init();
        String decryptPassword = Encr.decryptPassword(dev);
        System.out.println(decryptPassword);
```

**如果对您有所帮助，记得给小编点个赞哦，也可以店家小编的微信，372787553，进入程序猿/媛交流群！**