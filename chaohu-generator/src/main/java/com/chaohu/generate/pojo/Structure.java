package com.chaohu.generate.pojo;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Data
@Slf4j
@Accessors(chain = true)
public class Structure {

    /**
     * 父结构路径
     */
    private String parentPath;

    /**
     * 文件输出路径
     */
    private String outputPath;

    /**
     * 类名
     */
    private String className;

    /**
     * 文件类型后缀
     */
    private String fileSuffix;

    /**
     * 模版属性
     */
    private FtlParam ftlParam;

    /**
     * 将模版参数放入dataMap中
     */
    public Map<String, Object> initDataMap() {
        Map<String, Object> map = new HashMap<>(64);
        if (ftlParam.getApiPath() != null) {
            Field[] declaredFields = ftlParam.getClass().getDeclaredFields();
            Arrays.stream(declaredFields).forEach(filed -> {
                try {
                    map.put(filed.getName(), filed.get(ftlParam));
                } catch (IllegalAccessException e) {
                    log.error("数据初始胡失败: {}", e.getMessage());
                }
            });
        }
        return map;
    }
}
