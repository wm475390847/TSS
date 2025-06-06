package org.sprouts.tss.hardware;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 属性解析类
 *
 * @author wangmin
 * @date 2021/12/23 11:53 下午
 */
@Slf4j
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
            log.info("解析配置文件");

            File file = new File(System.getProperty("user.dir") + "/application.properties");
            try {
                InputStream inputStream = file.exists() ? Files.newInputStream(file.toPath()) :
                        Properties.class.getResourceAsStream("application.properties");

                if (inputStream == null) {
                    throw new RuntimeException("inputStream is null");
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                properties = new Properties();
                properties.load(bufferedReader);
            } catch (IOException e) {
                throw new RuntimeException(e);
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
