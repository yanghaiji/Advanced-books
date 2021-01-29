## Java 代码调用Shell 脚本

```java
//在单独的进程中执行指定的字符串命令。
Process exec(String command) 
//在单独的进程中执行指定命令和变量。  
Process exec(String[] cmdarray) 
//在指定环境的独立进程中执行指定命令和变量。
Process exec(String[] cmdarray, String[] envp) 
//在指定环境和工作目录的独立进程中执行指定的命令和变量。  
Process exec(String[] cmdarray, String[] envp, File dir) 
//在指定环境的单独进程中执行指定的字符串命令。 
Process exec(String command, String[] envp) 
//在有指定环境和工作目录的独立进程中执行指定的字符串命令。
Process exec(String command, String[] envp, File dir) 

```

代码示例

```java
package com.javayh.advanced.java.linux.shell;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>
 * 调用shell脚本
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-26
 */
@Slf4j
public class ShellSend {

    /**
     * 改变文件执行的权限
     * @param shellPath
     * @return
     * @throws Exception
     */
    public static boolean runChmod(String shellPath) throws Exception {
        // 添加shell的执行权限
        String chmod = "chmod +x " + shellPath;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(chmod);
            int waitFor = process.waitFor();
            if (waitFor != 0) {
                log.error("改变Shell脚本执行权限发生异常");
                return false;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
           log.info("脚本权限添加成功 >>>>[{}]",in.readLine());
        } catch (IOException | InterruptedException e) {
            throw e;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return true;
    }

    /**
     * 执行shell脚本
     *
     * @param shellPath shell脚本路劲
     * @param params    执行脚本所需参数
     * @return
     */
    public static String runShellCommand(String shellPath, String[] params) throws IOException, InterruptedException {
        String execShell = "/bin/sh " + shellPath;
        // 执行shell脚本时的参数拼接
        if (params != null && params.length > 0) {
            for (String param : params) {
                execShell += " " + param;
            }
        }
        return run(execShell);
    }

    public String runShell(String shPath){
        if(StringUtils.isNotBlank(shPath)){
            return run(shPath);
        }
        log.error("file path is null");
        return null;
    }

    private static String run(String shPath){
        Process process = null;
        BufferedReader in = null;
        try {
            process =Runtime.getRuntime().exec(shPath);
            int waitFor = process.waitFor();
            if(waitFor != 0){
                log.error("run shell error");
                return null;
            }
            in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String lineTxt;
            while ((lineTxt = in.readLine()) != null) {
                sb.append(lineTxt + "\n");
            }
            return sb.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("关闭输出流发生异常");
                }
            }
        }
        return null;
    }
}

```