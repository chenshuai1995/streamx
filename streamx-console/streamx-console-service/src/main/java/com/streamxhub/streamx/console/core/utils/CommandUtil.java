package com.streamxhub.streamx.console.core.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jim Chen
 */
@Slf4j
public class CommandUtil {

    public static int execute(String cmd) {

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        BufferedReader br = null;
        try {
            String prefix1 = isWindows ? "cmd" : "/bin/sh";
            String prefix2 = isWindows ? "/c" : "-c";

            // 这里务必是有命令前缀, 否则直接执行程序, 日志重定向是失败的. 例如 ping www.baidu.com > a.txt
            Process p = Runtime.getRuntime().exec(new String[]{prefix1, prefix2, cmd});

            // 0 表示正常
            int returnCode = p.waitFor();

            if (returnCode != 0) {
                String charsetName = isWindows ? "GB2312" : "UTF-8";

                // 这里务必是有命令前缀, 否则直接执行程序, 日志重定向是失败的. 例如 ping www.baidu.com > a.txt
                br = new BufferedReader(new InputStreamReader(p.getErrorStream(), charsetName));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                log.error("shell 执行失败: {}", sb);
            }

            log.info("cmd = {}, returnCode = {}", cmd, returnCode);
            return returnCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
