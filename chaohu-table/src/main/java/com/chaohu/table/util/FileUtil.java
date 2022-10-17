package com.chaohu.table.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

/**
 * @author wangmin
 */
public class FileUtil {

    /**
     * 获取资源文件在当前项目下的绝对路径
     *
     * @param relativePath 资源的相对路径
     * @return String 资源的绝对路径
     */
    public static String getResourcePath(String relativePath) {
        relativePath = relativePath.charAt(0) == '/' ? relativePath : "/" + relativePath;
        String str = Objects.requireNonNull(FileUtil.class.getResource(relativePath)).getPath();
        String path = null;
        try {
            path = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }
}
