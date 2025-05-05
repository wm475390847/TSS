package org.sprouts.tss.conner.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

/**
 * 属性解析类
 * 单例模式的一种实现，采用了静态内部类的方式实现了延迟加载（Lazy Initialization）和线程安全。
 * 单例模式确保在应用程序的生命周期内只创建一个类的实例
 *
 * @author wangmin
 * @date 2021/12/23 11:53 下午
 */
@Slf4j
public class Property {

    private Property() {
    }

    public static Properties parse() {
        return PropertyHolder.INSTANCE.properties;
    }

    public static Properties parse(String fileName) {
        PropertyHolder propertyHolder = new PropertyHolder(fileName);
        return propertyHolder.properties;
    }

    private static class PropertyHolder {
        private static final PropertyHolder INSTANCE = new PropertyHolder();
        private final Properties properties;

        private PropertyHolder() {
            String appEnv = System.getenv("APP_ENV");
            properties = appEnv == null ? loadProperties("env.properties") : loadProperties();
            log.info("配置文件加载完成");
            Optional.of(properties).filter(e -> !e.isEmpty()).orElseThrow(() -> new RuntimeException("执行环境或配置文件为空"));
            log.info("配置文件内容: {}", properties);
        }

        private PropertyHolder(String fileName) {
            log.info("加载配置文件");
            properties = loadProperties(fileName);
        }

        private static Properties loadProperties(String resourceName) {
            Properties properties = new Properties();
            try (InputStream resource = Property.class.getClassLoader().getResourceAsStream(resourceName)) {
                if (resource != null) {
                    properties.load(new InputStreamReader(resource, StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                throw new RuntimeException("加载配置文件失败", e);
            }
            return properties;
        }

        private static Properties loadProperties() {
            Properties properties = new Properties();
            String filePath = System.getProperty("user.dir");
            try {
                FileInputStream fileInputStream = new FileInputStream(filePath + "/env.properties");
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                properties.load(bufferedInputStream);
            } catch (IOException e) {
                log.error(e.toString());
            }
            return properties;
        }
    }
}
