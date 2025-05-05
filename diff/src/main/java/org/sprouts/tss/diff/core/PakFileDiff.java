package org.sprouts.tss.diff.core;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 压缩文件对比
 */
@Slf4j
public class PakFileDiff extends AbstractFileDiff {
    public PakFileDiff(Builder builder) {
        super(builder);
    }

    @Override
    public void diffFile() {
        try {
            // 计算两个压缩包的MD5值
            String md5_1 = calculateMD5(getOriginal());
            String md5_2 = calculateMD5(getRevised());

            // 比较MD5值
            if (md5_1.equals(md5_2)) {
                log.info("两个压缩包完全相同");
            } else {
                log.info("两个压缩包不相同，压缩包1MD5值{} 压缩包2MD5值{}", md5_1, md5_2);
            }
        } catch (Exception e) {
            throw new RuntimeException("计算MD5值时发生错误: " + e.getMessage());
        }
    }

    private String calculateMD5(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream fis = Files.newInputStream(Paths.get(filePath))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }
        byte[] digest = md.digest();
        return bytesToHex(digest);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static class Builder extends AbstractFileDiffBuilder {

        @Override
        protected IDiff buildFileDiff() {
            return new PakFileDiff(this);
        }
    }
}
