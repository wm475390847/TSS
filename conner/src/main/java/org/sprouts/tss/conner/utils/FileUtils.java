package org.sprouts.tss.conner.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static java.lang.String.format;

/**
 * @author wangmin
 * @date 2022/6/11 09:59
 */
@Slf4j
public class FileUtils {

    /**
     * 获取路径中的最后的部分，及最后的文件夹名或者文件名
     *
     * @param path 绝对路径或者相对路径
     * @return String 文件夹名或者文件名，路径为空则返回null
     */
    public static String getLastName(String path) {
        if (!StringUtils.isEmpty(path)) {
            String[] strings = path.split(String.format("/|\\%s", FileSystems.getDefault().getSeparator()));
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
        try (InputStream in = Files.newInputStream(Paths.get(filePath)); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取json文件
     *
     * @param filepath
     * @return
     */
    public static String readJsonFile(String filepath) {
        try {
            // 读取 JSON 文件内容到字符串
            return new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.info("文件读取失败:：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static final String propertiesPath = format("%s/properties/", System.getProperty("user.dir"));


    public static File readFile(String filePath) {
        return new File(Objects.requireNonNull(FileUtils.class.getResource(filePath)).getFile());
    }

    /**
     * 获取json文件流
     *
     * @param path     文件路径
     * @param fileName 文件名称
     * @return 读取的文件流
     */
    public static String readFile(String path, String fileName) {
        try (InputStream fileStream = FileUtils.class.getResourceAsStream(format("%s/%s", path, fileName))) {
            BufferedReader br;
            String readLine;
            StringBuilder sb = new StringBuilder();
            Assert.assertNotNull(fileStream, format("读取资源文件异常，文件流为空，文件路径为 %s", format("%s/%s", path, fileName)));
            br = new BufferedReader(new InputStreamReader(fileStream));
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            return sb.toString();
        } catch (IOException e) {
            log.error(format("读取资源文件异常,异常信息为%s", e.getMessage()));
            throw new RuntimeException(format("读取资源文件异常,异常信息为%s", e.getMessage()));
        }
    }
}
