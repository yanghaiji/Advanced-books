- [cd 命令](cd-命令)
- [ls 命令](ls-命令)
- [df 命令](df-命令)
- [du 命令](du-命令)
- [mkdir 命令](mkdir-命令)
- [pwd 命令](pwd-命令)
- [tree 命令](tree-命令)

### cd 命令

- 语法

  ```
  cd [dirName]
  ```

  其中 dirName 表示法可为绝对路径或相对路径。若目录名称省略，则变换至使用者的 home 目录 (也就是刚 login 时所在的目录)。

  另外，**~** 也表示为 home 目录 的意思， **.** 则是表示目前所在的目录， **..** 则表示目前目录位置的上一层目录。

- 示例

  - 跳转到指定目录

    ```
    cd /home/java
    ```

  - 返回上一级

    ```
    cd ../
    ```

### ls 命令

- 语法

  ```
  ls [-alrtAFR] [name...]
  ```

  **参数** :

  - -a 显示所有文件及目录 (**.** 开头的隐藏文件也会列出)
  - -l 除文件名称外，亦将文件型态、权限、拥有者、文件大小等资讯详细列出
  - -r 将文件以相反次序显示(原定依英文字母次序)
  - -t 将文件依建立时间之先后次序列出
  - -A 同 -a ，但不列出 "." (目前目录) 及 ".." (父目录)
  - -F 在列出的文件名称后加一符号；例如可执行档则加 "*", 目录则加 "/"
  - -R 若目录下有文件，则以下之文件亦皆依序列出

- 示例

  - 列出根目录(\)下的所有目录

    ```
    ls /
    ```

  - 列出目前工作目录下所有名称是 n 开头的文件

    ```
     ls -ltr n*
    ```

    越新的排越后面 

### df 命令

- 语法

  ```
  df [选项]... [FILE]...
  ```

  - 文件-a, --all 包含所有的具有 0 Blocks 的文件系统
  - 文件--block-size={SIZE} 使用 {SIZE} 大小的 Blocks
  - 文件-h, --human-readable 使用人类可读的格式(预设值是不加这个选项的...)
  - 文件-H, --si 很像 -h, 但是用 1000 为单位而不是用 1024
  - 文件-i, --inodes 列出 inode 资讯，不列出已使用 block
  - 文件-k, --kilobytes 就像是 --block-size=1024
  - 文件-l, --local 限制列出的文件结构
  - 文件-m, --megabytes 就像 --block-size=1048576
  - 文件--no-sync 取得资讯前不 sync (预设值)
  - 文件-P, --portability 使用 POSIX 输出格式
  - 文件--sync 在取得资讯前 sync
  - 文件-t, --type=TYPE 限制列出文件系统的 TYPE
  - 文件-T, --print-type 显示文件系统的形式
  - 文件-x, --exclude-type=TYPE 限制列出文件系统不要显示 TYPE
  - 文件-v (忽略)
  - 文件--help 显示这个帮手并且离开
  - 文件--version 输出版本资讯并且离开

- 示例

  - 显示文件系统的磁盘使用情况统计

    ```
    # df
    Filesystem     1K-blocks     Used Available Use% Mounted on
    /dev/sda2       30929148 15634232  15294916  51% /
    devtmpfs         8194356        0   8194356   0% /dev
    tmpfs            8206404        0   8206404   0% /dev/shm
    ```

    - Filesystem  文件系统的名称
    - 1K-blocks    总空间
    - Used             已使用的
    - Available      剩余的
    - Use                使用率
    - Mounted on 所在位置

  - 格式化df命令

    ```
    # df -h
    Filesystem      Size  Used Avail Use% Mounted on
    /dev/sda2        30G   15G   15G  51% /
    devtmpfs        7.9G     0  7.9G   0% /dev
    tmpfs           7.9G     0  7.9G   0% /dev/shm
    ```

### du 命令

- 语法

  ```
  du [-abcDhHklmsSx][-L <符号连接>][-X <文件>][--block-size][--exclude=<目录或文件>][--max-depth=<目录层数>][--help][--version][目录或文件]
  ```

  **参数说明**：

  - -a或-all 显示目录中个别文件的大小。
  - -b或-bytes 显示目录或文件大小时，以byte为单位。
  - -c或--total 除了显示个别目录或文件的大小外，同时也显示所有目录或文件的总和。
  - -D或--dereference-args 显示指定符号连接的源文件大小。
  - -h或--human-readable 以K，M，G为单位，提高信息的可读性。
  - -H或--si 与-h参数相同，但是K，M，G是以1000为换算单位。
  - -k或--kilobytes 以1024 bytes为单位。
  - -l或--count-links 重复计算硬件连接的文件。
  - -L<符号连接>或--dereference<符号连接> 显示选项中所指定符号连接的源文件大小。
  - -m或--megabytes 以1MB为单位。
  - -s或--summarize 仅显示总计。
  - -S或--separate-dirs 显示个别目录的大小时，并不含其子目录的大小。
  - -x或--one-file-xystem 以一开始处理时的文件系统为准，若遇上其它不同的文件系统目录则略过。
  - -X<文件>或--exclude-from=<文件> 在<文件>指定目录或文件。
  - --exclude=<目录或文件> 略过指定的目录或文件。
  - --max-depth=<目录层数> 超过指定层数的目录后，予以忽略。
  - --help 显示帮助。
  - --version 显示版本信息。

- 示例

  - 显示目录或者文件所占空间

    ```
    # du
    73092   ./mysql
    840     ./spring
    ```

  - 格式化

    ```
    # du logs/ -h
    72M     logs/mysql
    840K    logs/spring
    ```

### mkdir 命令

- 语法

  ```
  mkdir [-p] dirName
  ```

  **参数说明**：

  - -p 确保目录名称存在，不存在的就建一个。

- 示例

  - 创建一个test文件

    ```
    mkdir test
    ```

### pwd命令

- 语法

  ```
  pwd [--help][--version]
  ```

  **参数说明:**

  - --help 在线帮助。
  - --version 显示版本信息。

- 示例

  - 查看当前所在目录

    ```
    # pwd
    /root
    ```

### tree命令

- 语法

  ```
  tree [-aACdDfFgilnNpqstux][-I <范本样式>][-P <范本样式>][目录...]
  ```

  **参数说明**：

  - -a 显示所有文件和目录。
  - -A 使用ASNI绘图字符显示树状图而非以ASCII字符组合。
  - -C 在文件和目录清单加上色彩，便于区分各种类型。
  - -d 显示目录名称而非内容。
  - -D 列出文件或目录的更改时间。
  - -f 在每个文件或目录之前，显示完整的相对路径名称。
  - -F 在执行文件，目录，Socket，符号连接，管道名称名称，各自加上"*","/","=","@","|"号。
  - -g 列出文件或目录的所属群组名称，没有对应的名称时，则显示群组识别码。
  - -i 不以阶梯状列出文件或目录名称。
  - -L level 限制目录显示层级。
  - -l 如遇到性质为符号连接的目录，直接列出该连接所指向的原始目录。
  - -n 不在文件和目录清单加上色彩。
  - -N 直接列出文件和目录名称，包括控制字符。
  - -p 列出权限标示。
  - -P<范本样式> 只显示符合范本样式的文件或目录名称。
  - -q 用"?"号取代控制字符，列出文件和目录名称。
  - -s 列出文件或目录大小。
  - -t 用文件和目录的更改时间排序。
  - -u 列出文件或目录的拥有者名称，没有对应的名称时，则显示用户识别码。
  - -x 将范围局限在现行的文件系统中，若指定目录下的某些子目录，其存放于另一个文件系统上，则将该子目录予以排除在寻找范围外。

- 示例

  - 树状图列出当前目录结构

    ```
    # tree
    .
    ├── anaconda-ks.cfg
    ├── log.log
    ├── logs
    │   ├── nacos
    │   │   ├── config.log
    │   │   ├── naming.log
    │   │   ├── naming.log.1
    │   │   ├── naming.log.2
    │   │   ├── naming.log.3
    │   │   ├── naming.log.4
    │   │   ├── naming.log.5
    │   │   ├── naming.log.6
    │   │   └── naming.log.7
    
    ```