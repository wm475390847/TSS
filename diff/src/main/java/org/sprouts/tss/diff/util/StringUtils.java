package org.sprouts.tss.diff.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 工具类
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    // 定义字符集
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    // 定义字符串长度
    private static final int LENGTH = 8;

    // 用于存储已经生成过的字符串
    private static final Set<String> generatedStrings = new HashSet<>();

    /**
     * 生成唯一随机字符串
     *
     * @return 唯一随机字符串
     */
    public static String genUniqueRandomStr() {
        // 创建随机数生成器
        Random random = new Random();
        // 创建StringBuilder对象
        StringBuilder sb = new StringBuilder(LENGTH);
        // 循环生成随机字符串
        while (true) {
            sb.setLength(0); // 清空StringBuilder
            for (int i = 0; i < LENGTH; i++) {
                int index = random.nextInt(CHARACTERS.length());
                sb.append(CHARACTERS.charAt(index));
            }
            String result = sb.toString();
            if (!generatedStrings.contains(result)) {
                generatedStrings.add(result); // 添加到集合中
                return result;
            }
        }
    }
}
