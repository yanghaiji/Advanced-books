## Shell 编程简介

Shell 是一个命令解释权，它为用户提供了一个向 Linux 内核发送请求以便运行程序界面系统级程序，
用户可以用 Shell 来启动、挂起、停止甚至是编写一些程序。

### 第一个Shell脚本

```shell script
#!/bin/bash

for ((i=0; i<10; i++)); do
	echo "Hello Shell"_$i
done

```

- #! 是一个约定的标记，它告诉系统这个脚本需要什么解释器来执行，即使用哪一种 Shell,这里是固定的写法

- 做一次循环输出

### 运行 Shell 脚本有两种方法：

**1、作为可执行程序**

将上面的代码保存为 test.sh，并 cd 到相应目录：

```
chmod +x ./test.sh  #使脚本具有执行权限
./test.sh  #执行脚本
```

注意，一定要写成 **./test.sh**，而不是 **test.sh**，运行其它二进制的程序也一样，直接写 test.sh，linux 系统会去 PATH 里寻找有没有叫 test.sh 的，而只有 /bin, /sbin, /usr/bin，/usr/sbin 等在 PATH 里，你的当前目录通常不在 PATH 里，所以写成 test.sh 是会找不到命令的，要用 ./test.sh 告诉系统说，就在当前目录找。

**2、作为解释器参数**

这种运行方式是，直接运行解释器，其参数就是 shell 脚本的文件名，如：

```
/bin/sh test.sh
```

  