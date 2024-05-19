package com.chaohu.generate.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
public class StringUtil {
    private static final Pattern LINE_PATTERN = Pattern.compile("[_|-](\\w)");
    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线并将第一个下划线去除
     *
     * @param str str
     * @return result
     */
    public static String humpToLineReplaceFirst(String str) {
        str = humpToLine(str);
        return str.startsWith("_") ? str.replaceFirst("_", "") : str;
    }

    /**
     * 驼峰转下划线
     *
     * @param str str
     * @return result
     */
    public static String humpToLine(String str) {
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param str              str
     * @param firstIsUpperCase 首字母是否大写
     * @return result
     */
    public static String lineToHump(String str, Boolean firstIsUpperCase) {
        str = isContainUpperCase(str) ? str : str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        if (firstIsUpperCase) {
            if (StringUtils.isNotEmpty(sb.toString())) {
                String s = sb.substring(0, 1).toUpperCase();
                return s + sb.substring(1, sb.length());
            } else {
                return sb.toString();
            }
        } else {
            return sb.toString();
        }
    }

    /**
     * 字符串首字母大写
     *
     * @param str 字符串
     * @return 新的字符串
     */
    public static String firstUpperCase(String str) {
        String first = str.substring(0, 1).toUpperCase();
        return first + str.substring(1);
    }

    /**
     * 判断字符串是否包含大写字母
     *
     * @param str 字符串
     * @return boolean
     */
    private static boolean isContainUpperCase(String str) {
        Set<Boolean> set = new HashSet<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isLowerCase(c)) {
                set.add(false);
            }
        }
        return set.contains(false);
    }
}
