package com.chaohu.conner.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 属性解析类
 *
 * @author wangmin
 * @date 2021/12/23 11:53 下午
 */
public class Property {
    private static volatile Property property = null;
    private static Properties properties;

    /**
     * 解析本地配置文件
     *
     * @return 配置文件信息
     */
    public Properties parse() {
        if (properties == null) {
            ClassLoader classLoader = Property.class.getClassLoader();
            InputStream resource = classLoader.getResourceAsStream("env.properties");
            properties = new Properties();
            if (resource != null) {
                // 重新编码
                InputStreamReader inputStreamReader = new InputStreamReader(resource, StandardCharsets.UTF_8);
                try {
                    properties.load(inputStreamReader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    public static Property getInstance() {
        if (property == null) {
            synchronized (Property.class) {
                if (property == null) {
                    property = new Property();
                }
            }
        }
        return property;
    }
}
