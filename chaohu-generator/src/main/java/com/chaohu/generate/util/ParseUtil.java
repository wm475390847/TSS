package com.chaohu.generate.util;

import com.chaohu.generate.enums.KeywordEnum;

import java.util.Arrays;

/**
 * @author wangmin
 * @date 2022/5/17 13:05
 */
public class ParseUtil {

    /**
     * 获取包名
     *
     * @param outputPath 输出路径
     * @return 包名
     */
    public static String getPackageName(String outputPath) {
        String[] strings = outputPath.split("/");
        int s = 0;
        for (int i = 0; i < strings.length; i++) {
            s = "java".equals(strings[i]) ? i + 1 : s;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = s; i < strings.length; i++) {
            sb.append(strings[i]).append(".");
        }
        return sb.replace(sb.length() - 1, sb.length(), "").toString();
    }

    /**
     * 获取输出路径
     *
     * @param outputPath 输出路径
     * @return 输出路径
     */
    public static String getOutputPath(String outputPath) {
        return outputPath.endsWith("/") ? outputPath : outputPath + "/";
    }

    /**
     * 获取类名
     * <P>返回类名：pathMethodSuffix</P>
     *
     * @param apiPath   接口路径
     * @param apiMethod 接口请求方式
     * @param apiSuffix 接口后缀
     * @return className
     */
    public static String getClassName(String apiPath, String apiMethod, String apiSuffix) {
        StringBuilder sb = new StringBuilder();
        String[] pathList = getPathList(apiPath);
        int length = pathList.length;
        int index = length <= 1 ? length - 1 : length - 2;
        for (int i = index; i < length; i++) {
            sb.append(StringUtil.firstUpperCase(pathList[i]));
        }
        String name = StringUtil.firstUpperCase(sb.toString());
        if (apiMethod != null) {
            name = name + StringUtil.lineToHump(apiMethod.toLowerCase(), true);
        }
        String className = apiSuffix == null ? name : name + apiSuffix;
        className = className.replaceAll("[^a-zA-Z]", "");
        return className;
    }

    /**
     * 获取路径list
     *
     * @param apiPath 接口路径
     * @return 接口路径集合
     */
    private static String[] getPathList(String apiPath) {
        apiPath = apiPath.replaceAll("[{}]", "");
        apiPath = apiPath.startsWith("/") ? apiPath.replaceFirst("/", "") : apiPath;
        apiPath = apiPath.endsWith("/") ? apiPath.substring(0, apiPath.length() - 1) : apiPath;
        String[] pathList = apiPath.split("/");
        return Arrays.stream(pathList)
                .map(KeywordEnum::transferKeyword)
                .map(e -> StringUtil.lineToHump(e, true))
                .toArray(String[]::new);
    }
}
