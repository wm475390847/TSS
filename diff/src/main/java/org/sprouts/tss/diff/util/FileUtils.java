package org.sprouts.tss.diff.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class FileUtils {

    /**
     * 解析路径
     *
     * @param input url 或本地路径
     * @return FileDTO
     */
    public static FileInfo parsePath(String input) {
        // 使用正则表达式提取 URL 或本地路径
        Pattern pattern = Pattern.compile("<url.*?>(.+?)</url>|(\\S+)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            // 获取匹配到的路径
            String fullPath = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            // 提取路径、文件名和扩展名
            int lastSlashIndex = fullPath.lastIndexOf('/');
            return getFile(fullPath, lastSlashIndex);
        } else {
            throw new IllegalArgumentException("Invalid input format");
        }
    }

    @NotNull
    private static FileInfo getFile(String fullPath, int lastSlashIndex) {
        int lastDotIndex = fullPath.lastIndexOf('.');
        // 如果没有扩展名，避免负索引
        if (lastDotIndex < 0 || lastDotIndex < lastSlashIndex) {
            lastDotIndex = fullPath.length();
        }
        String path = fullPath.substring(0, lastSlashIndex + 1);
        String fileName = fullPath.substring(lastSlashIndex + 1, lastDotIndex);
        String extension = lastDotIndex + 1 < fullPath.length() ? fullPath.substring(lastDotIndex + 1) : "";

        FileInfo fileDTO = new FileInfo();
        fileDTO.setPath(path);
        fileDTO.setFileName(fileName);
        fileDTO.setFullPath(fullPath);
        fileDTO.setExtension(extension);
        return fileDTO;
    }

    /**
     * 下载文件到本地
     *
     * @param sourceFileUrl 源文件地址
     * @param targetFile    下载后的文件
     * @return 本地文件路径
     */
    public static String uploadFile(String sourceFileUrl, String targetFile) {
        log.info("下载源文件: {}", sourceFileUrl);
        long startTime = System.currentTimeMillis();
        try {
            // 打开连接
            URL url = new URL(sourceFileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 设置连接超时时间
            connection.setConnectTimeout(10000);
            // 设置读取超时时间
            connection.setReadTimeout(10000);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取输入流并写入文件
                try (InputStream inputStream = connection.getInputStream();
                     OutputStream outputStream = Files.newOutputStream(Paths.get(targetFile))) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    log.info("文件下载完成，保存路径 {}，耗时 {}ms", targetFile, System.currentTimeMillis() - startTime);
                }
            } else {
                throw new RuntimeException("文件下载异常，错误码: " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("文件下载异常: " + e.getMessage());
        }
        return targetFile;
    }

    /**
     * 解压zip文件
     *
     * @param zipFile   zip文件路径
     * @param targetDir 解压到目标目录
     * @return 解压后的目录路径
     */
    public static String unzipFile(String zipFile, String targetDir) {
        long startTime = System.currentTimeMillis();
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(zipFile)))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File newFile = new File(targetDir, zipEntry.getName());
                File parentDir = newFile.getParentFile();
                // 在需要时创建目录
                if (!parentDir.exists()) {
                    log.debug("创建目录: {}", parentDir.mkdirs());
                }
                if (!zipEntry.isDirectory()) {
                    // 如果是文件，写入内容
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
            log.info("[ZIP] 文件{}解压完成，保存路径: {}，耗时: {}ms", zipFile, targetDir, System.currentTimeMillis() - startTime);
            return targetDir;
        } catch (IOException e) {
            throw new RuntimeException("文件读写异常: " + e.getMessage());
        }
    }

    /**
     * 解压tar.gz文件
     *
     * @param tgzFile   tar.gz文件路径
     * @param targetDir 解压到目标目录
     * @return 解压后的目录路径
     */
    public static String unTgzFile(String tgzFile, String targetDir) {
        long startTime = System.currentTimeMillis();
        Path targetPath = Paths.get(targetDir);
        try (FileInputStream fis = new FileInputStream(tgzFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             TarArchiveInputStream tarInput = new TarArchiveInputStream(
                     CompressorStreamFactory.getSingleton().createCompressorInputStream(bis))) {
            TarArchiveEntry entry;
            while ((entry = tarInput.getNextEntry()) != null) {
                Path entryPath = targetPath.resolve(entry.getName());
                if (entry.isDirectory()) {
                    // 确保目录存在
                    Files.createDirectories(entryPath);
                    log.debug("创建目录: {}", entryPath);
                } else {
                    // 确保父目录存在
                    Files.createDirectories(entryPath.getParent());
                    try (OutputStream out = Files.newOutputStream(entryPath)) {
                        IOUtils.copy(tarInput, out);
                    }
                    log.debug("解压文件: {}", entryPath);
                }
            }
            log.info("[TAR.GZ] 文件{}解压完成，保存路径: {}，耗时: {}ms", tgzFile, targetDir, System.currentTimeMillis() - startTime);
        } catch (IOException e) {
            throw new RuntimeException("文件读写异常: " + e.getMessage());
        } catch (CompressorException e) {
            throw new RuntimeException("文件解压异常: " + e.getMessage());
        }
        return targetDir;
    }

    @Data
    public static class FileInfo {
        private String fullPath;
        private String path;
        private String fileName;
        private String extension;
    }
}
