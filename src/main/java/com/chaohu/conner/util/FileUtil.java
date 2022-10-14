package com.chaohu.conner.util;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author wangmin
 * @date 2022/6/11 09:59
 */
public class FileUtil {

    /**
     * 获取路径中的最后的部分，及最后的文件夹名或者文件名
     *
     * @param path 绝对路径或者相对路径
     * @return String 文件夹名或者文件名，路径为空则返回null
     */
    public static String getLastName(String path) {
        if (!StringUtils.isEmpty(path)) {
            String[] strings = path.split(String.format("/|\\%s", System.getProperty("file.separator")));
            int length = strings.length;
            if (length > 0) {
                return strings[length - 1];
            }
        }
        return null;
    }

    /**
     * 获取文件信息的二进制数据
     *
     * @param filePath 文件路径
     * @return byte[]
     */
    public static byte[] getFileBytes(String filePath) {
        try (InputStream in = new FileInputStream(filePath);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
