package org.sprouts.tss.generator.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件工具类
 *
 * @author wangmin
 * @date 2022/6/11 09:59
 */
@Slf4j
public class FileUtils {

    /**
     * 从文件系统中读取指定路径的文件内容，并将其转换为字符串。
     *
     * @param filePath 文件的绝对路径或相对路径。
     * @return 文件内容的字符串表示。
     * @throws RuntimeException 如果读取文件过程中发生异常。
     */
    public static String readFileFromSystemToString(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Files.newInputStream(Paths.get(filePath)), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line);
            }
        } catch (IOException e) {
            log.error("读取资源文件异常, 异常信息为: {}", e.getMessage());
            throw new RuntimeException(String.format("读取文件异常, 异常信息为%s", e.getMessage()));
        }
        return contentBuilder.toString();
    }

    /**
     * 从类路径中读取指定路径的资源文件内容，并将其转换为字符串。
     *
     * @param resourcePath 相对于类路径的资源路径。
     * @return 资源文件内容的字符串表示。
     * @throws RuntimeException 如果读取资源文件过程中发生异常或文件流为空。
     */
    public static String readResourceFileToString(String resourcePath) {
        try (InputStream fileStream = FileUtils.class.getResourceAsStream(resourcePath)) {
            if (fileStream == null) {
                throw new RuntimeException(String.format("读取资源文件异常，文件流为空，文件路径为 %s", resourcePath));
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String readLine;
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            return sb.toString();
        } catch (IOException e) {
            log.error("读取资源文件异常, 异常信息为: {}", e.getMessage());
            throw new RuntimeException(String.format("读取资源文件异常, 异常信息为%s", e.getMessage()));
        }
    }

}
