## 系统管理常用命令

- [useradd命令](#useradd命令)
- [userconf命令](#userconf命令)
- [userdel命令](#userdel命令)
- [usermod命令](#usermod命令)
- [groupadd命令](#groupadd命令)
- [groupdel命令](#groupdel命令)
- [groupmod命令](#groupmod命令)
- [passwd命令](#passwd命令)
- [sudo命令](#sudo命令)
- [su命令](#su命令)
- [ps命令](#ps命令)
- [top命令](#top命令)

### useradd命令

useradd 可用来建立用户帐号。帐号建好之后，再用 passwd 设定帐号的密码。而可用 userdel 删除帐号。使用 useradd 指令所建立的帐号，实际上是保存在 /etc/passwd 文本文件中。

- 语法

  ```shell
  useradd [-mMnr][-c <备注>][-d <登入目录>][-e <有效期限>][-f <缓冲天数>][-g <群组>][-G <群组>][-s <shell>][-u <uid>][用户帐号]
  
  或者
  useradd -D [-b][-e <有效期限>][-f <缓冲天数>][-g <群组>][-G <群组>][-s <shell>]
  ```

  **参数说明**：

  - -c<备注> 　加上备注文字。备注文字会保存在passwd的备注栏位中。
  - -d<登入目录> 　指定用户登入时的起始目录。
  - -D 　变更预设值．
  - -e<有效期限> 　指定帐号的有效期限。
  - -f<缓冲天数> 　指定在密码过期后多少天即关闭该帐号。
  - -g<群组> 　指定用户所属的群组。
  - -G<群组> 　指定用户所属的附加群组。
  - -m 　自动建立用户的登入目录。
  - -M 　不要自动建立用户的登入目录。
  - -n 　取消建立以用户名称为名的群组．
  - -r 　建立系统帐号。
  - -s<shell>　 　指定用户登入后所使用的shell。
  - -u<uid> 　指定用户ID。

- 示例

  添加一般用户

  ```
  # useradd tt
  ```

  为添加的用户指定相应的用户组

  ```
  # useradd -g root tt
  ```

  创建一个系统用户

  ```
  # useradd -r tt
  ```

  为新添加的用户指定home目录

  ```
  # useradd -d /home/myd tt
  ```

  建立用户且制定ID

  ```
  # useradd caojh -u 544
  ```

### userconf命令

- 语法

  ```
  userconf [--addgroup <群组>][--adduser <用户ID><群组><用户名称><shell>][--delgroup <群组>][--deluser <用户ID>][--help]
  ```

  **参数说明**：

  - --addgroup<群组> 　新增群组。
  - --adduser<用户ID><群组><用户名称><shell> 　新增用户帐号。
  - --delgroup<群组> 　删除群组。
  - --deluser<用户ID> 　删除用户帐号。
  - --help 　显示帮助。

- 示例

  新增用户

  ```
  # userconf --adduser 666 tt lord /bin/bash //新增用户账号
  ```

### userdel命令

- 语法

  ```
  userdel [-r][用户帐号]
  ```

  **参数说明**：

  - -r 　删除用户登入目录以及目录中所有文件

- 示例

  删除用户账号

  ```
  # userdel hnlinux
  ```

  

### usermod命令

- 语法

  ```
  usermod [-LU][-c <备注>][-d <登入目录>][-e <有效期限>][-f <缓冲天数>][-g <群组>][-G <群组>][-l <帐号名称>][-s <shell>][-u <uid>][用户帐号]
  ```

  **参数说明**：

  - -c<备注> 　修改用户帐号的备注文字。
  - -d登入目录> 　修改用户登入时的目录。
  - -e<有效期限> 　修改帐号的有效期限。
  - -f<缓冲天数> 　修改在密码过期后多少天即关闭该帐号。
  - -g<群组> 　修改用户所属的群组。
  - -G<群组> 　修改用户所属的附加群组。
  - -l<帐号名称> 　修改用户帐号名称。
  - -L 　锁定用户密码，使密码无效。
  - -s<shell> 　修改用户登入后所使用的shell。
  - -u<uid> 　修改用户ID。
  - -U 　解除密码锁定。

- 示例

  更改登录目录

  ```
  # usermod -d /home/hnlinux root
  ```

  改变用户的uid

  ```
  # usermod -u 777 root
  ```

  

### groupadd命令

groupadd 命令用于创建一个新的工作组，新工作组的信息将被添加到系统文件中。

相关文件:

1. /etc/group 组账户信息。
2. /etc/gshadow 安全组账户信息。
3. /etc/login.defs Shadow密码套件配置。

- 语法

  ```
  groupadd [-g gid [-o]] [-r] [-f] group
  ```

  **参数说明：**

  - -g：指定新建工作组的 id；
  - -r：创建系统工作组，系统工作组的组ID小于 500；
  - -K：覆盖配置文件 "/ect/login.defs"；
  - -o：允许添加组 ID 号不唯一的工作组。
  - -f,--force: 如果指定的组已经存在，此选项将失明了仅以成功状态退出。当与 -g 一起使用，并且指定的GID_MIN已经存在时，选择另一个唯一的GID（即-g关闭）。

- 示例

  创建一个新的组，并添加组 ID。

  ```
  ＃groupadd －g 344 test
  ```

  此时在 /etc/group 文件中产生一个组 ID（GID）是 344 的项目。
  
### groupdel命令

- 语法

  ```
  groupdel [群组名称]
  ```

- 示例

  删除一个群组

  ```
  # groupdel test
  ```

### groupmod命令

- 语法

  ```
  groupmod [-g <群组识别码> <-o>][-n <新群组名称>][群组名称]
  ```

  **参数**：

  - -g <群组识别码> 　设置欲使用的群组识别码。
  - -o 　重复使用群组识别码。
  - -n <新群组名称> 　设置欲使用的群组名称。

- 示例

  修改组名

  ```
  [root@test.com ~]# groupadd linuxso 
  [root@test.com ~]# tail -1 /etc/group 
  linuxso:x:500: 
  [root@test.com ~]# tail -1 /etc/group 
  linuxso:x:500: 
  [root@test.com ~]# groupmod -n linux linuxso 
  [root@test.com ~]# tail -1 /etc/group 
  linux:x:500:
  ```  

### passwd命令

- 语法

  ```
  passwd [-k] [-l] [-u [-f]] [-d] [-S] [username]
  ```

  **必要参数**：

  - -d 删除密码
  - -f 强迫用户下次登录时必须修改口令
  - -w 口令要到期提前警告的天数
  - -k 更新只能发送在过期之后
  - -l 停止账号使用
  - -S 显示密码信息
  - -u 启用已被停止的账户
  - -x 指定口令最长存活期
  - -g 修改群组密码
  - 指定口令最短存活期
  - -i 口令过期后多少天停用账户

- 示例

  修改用户密码

  ```
  # passwd test  //设置test用户的密码
  Enter new UNIX password:  //输入新密码，输入的密码无回显
  Retype new UNIX password:  //确认密码
  passwd: password updated successfully
  # 
  ```

  显示账号密码信息

  ```
  # passwd -S test
  test P 05/13/2010 0 99999 7 -1
  ```

  删除用户密码

  ```
  # passwd -d lx138 
  passwd: password expiry information changed.
  ```

### sudo命令

- 语法

  ```
  sudo [ -b ] [ -p prompt ] [ -u username/#uid] -s
  ```

  **参数说明**：

  - -V 显示版本编号
  - -h 会显示版本编号及指令的使用方式说明
  - -l 显示出自己（执行 sudo 的使用者）的权限
  - -v 因为 sudo 在第一次执行时或是在 N 分钟内没有执行（N 预设为五）会问密码，这个参数是重新做一次确认，如果超过 N 分钟，也会问密码
  - -k 将会强迫使用者在下一次执行 sudo 时问密码（不论有没有超过 N 分钟）
  - -b 将要执行的指令放在背景执行
  - -p prompt 可以更改问密码的提示语，其中 %u 会代换为使用者的帐号名称， %h 会显示主机名称
  - -u username/#uid 不加此参数，代表要以 root 的身份执行指令，而加了此参数，可以以 username 的身份执行指令（#uid 为该 username 的使用者号码）
  - -s 执行环境变数中的 SHELL 所指定的 shell ，或是 /etc/passwd 里所指定的 shell
  - -H 将环境变数中的 HOME （家目录）指定为要变更身份的使用者家目录（如不加 -u 参数就是系统管理者 root ）
  - command 要以系统管理者身份（或以 -u 更改为其他人）执行的指令

- 示例

  sudo命令使用

  ```
  $ sudo ls
  [sudo] password for hnlinux: 
  hnlinux is not in the sudoers file. This incident will be reported.
  ```

  指定用户执行命令

  ```
  # sudo -u userb ls -l
  ```

### su命令

- 语法

  ```
  su [-fmp] [-c command] [-s shell] [--help] [--version] [-] [USER [ARG]]
  ```

  **参数说明**：

  - -f 或 --fast 不必读启动档（如 csh.cshrc 等），仅用于 csh 或 tcsh
  - -m -p 或 --preserve-environment 执行 su 时不改变环境变数
  - -c command 或 --command=command 变更为帐号为 USER 的使用者并执行指令（command）后再变回原来使用者
  - -s shell 或 --shell=shell 指定要执行的 shell （bash csh tcsh 等），预设值为 /etc/passwd 内的该使用者（USER） shell
  - --help 显示说明文件
  - --version 显示版本资讯
  - \- -l 或 --login 这个参数加了之后，就好像是重新 login 为该使用者一样，大部份环境变数（HOME SHELL USER等等）都是以该使用者（USER）为主，并且工作目录也会改变，如果没有指定 USER ，内定是 root
  - USER 欲变更的使用者帐号
  - ARG 传入新的 shell 参数

- 示例

  变更帐号为 root 并在执行 ls 指令后退出变回原使用者

  ```
  su -c ls root
  ```

  变更帐号为 root 并传入 -f 参数给新执行的 shell

  ```
  su root -f
  ```

  变更帐号为 clsung 并改变工作目录至 clsung 的家目录（home dir）

  ```
  su - clsung
  ```
  
### ps命令

- 语法

  ```
  ps [options] [--help]
  ```

  **参数**：

  - ps 的参数非常多, 在此仅列出几个常用的参数并大略介绍含义

  - -A 列出所有的进程

  - -w 显示加宽可以显示较多的资讯

  - -au 显示较详细的资讯

  - -aux 显示所有包含其他使用者的行程

  - au(x) 输出格式 :

    ```
    USER PID %CPU %MEM VSZ RSS TTY STAT START TIME COMMAND
    ```

    - USER: 行程拥有者
    - PID: pid
    - %CPU: 占用的 CPU 使用率
    - %MEM: 占用的记忆体使用率
    - VSZ: 占用的虚拟记忆体大小
    - RSS: 占用的记忆体大小
    - TTY: 终端的次要装置号码 (minor device number of tty)
    - STAT: 该行程的状态:
      - D: 无法中断的休眠状态 (通常 IO 的进程)
      - R: 正在执行中
      - S: 静止状态
      - T: 暂停执行
      - Z: 不存在但暂时无法消除
      - W: 没有足够的记忆体分页可分配
      - <: 高优先序的行程
      - N: 低优先序的行程
      - L: 有记忆体分页分配并锁在记忆体内 (实时系统或捱A I/O)
    - START: 行程开始时间
    - TIME: 执行的时间
    - COMMAND:所执行的指令

- 示例

  查找制定进程格式：

  ```
  ps -ef | grep 进程关键字
  ```

  例如显示 php 的进程：

  ```
  # ps -ef | grep php
  root       794     1  0  2020 ?        00:00:52 php-fpm: master process (/etc/php/7.3/fpm/php-fpm.conf)
  www-data   951   794  0  2020 ?        00:24:15 php-fpm: pool www
  www-data   953   794  0  2020 ?        00:24:14 php-fpm: pool www
  www-data   954   794  0  2020 ?        00:24:29 php-fpm: pool www
  ...
  ```

  显示进程信息：

  ```
  # ps -A 
  PID TTY     TIME CMD
    1 ?    00:00:02 init
    2 ?    00:00:00 kthreadd
    3 ?    00:00:00 migration/0
  ```

  显示指定用户信息

  ```
  # ps -u root //显示root进程用户信息
   PID TTY     TIME CMD
    1 ?    00:00:02 init
    2 ?    00:00:00 kthreadd
    3 ?    00:00:00 migration/0
  ```

  显示所有进程信息，连同命令行

  ```
  # ps -ef //显示所有命令，连带命令行
  UID    PID PPID C STIME TTY     TIME CMD
  root     1   0 0 10:22 ?    00:00:02 /sbin/init
  root     2   0 0 10:22 ?    00:00:00 [kthreadd]
  ```  

### top命令

- 语法

  ```
  top [-] [d delay] [q] [c] [S] [s] [i] [n] [b]
  ```

  **参数说明**：

  - d : 改变显示的更新速度，或是在交谈式指令列( interactive command)按 s
  - q : 没有任何延迟的显示速度，如果使用者是有 superuser 的权限，则 top 将会以最高的优先序执行
  - c : 切换显示模式，共有两种模式，一是只显示执行档的名称，另一种是显示完整的路径与名称
  - S : 累积模式，会将己完成或消失的子行程 ( dead child process ) 的 CPU time 累积起来
  - s : 安全模式，将交谈式指令取消, 避免潜在的危机
  - i : 不显示任何闲置 (idle) 或无用 (zombie) 的行程
  - n : 更新的次数，完成后将会退出 top
  - b : 批次档模式，搭配 "n" 参数一起使用，可以用来将 top 的结果输出到档案内

- 示例

  显示进程信息

  ```
  # top
  ```

  显示完整命令

  ```
  # top -c
  ```

  以批处理模式显示程序信息

  ```
  # top -b
  ```

  以累积模式显示程序信息

  ```
  # top -S
  ```

  设置信息更新次数

  ```
  top -n 2
  
  //表示更新两次后终止更新显示
  ```

  设置信息更新时间

  ```
  # top -d 3
  
  //表示更新周期为3秒
  ```

  显示指定的进程信息

  ```
  # top -p 139
  
  //显示进程号为139的进程信息，CPU、内存占用率等
  ```

  显示更新十次后退出

  ```
  top -n 10
  ```

  使用者将不能利用交谈式指令来对行程下命令

  ```
  top -s
  ```







