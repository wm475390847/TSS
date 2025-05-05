package org.sprouts.tss.generator.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 类型枚举
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Getter
public enum ValTypeEnum {
    /**
     * 对象
     */
    OBJECT(Collections.singletonList("object"), 'O', JSONObject.class),

    /**
     * 数组
     */
    LIST(Collections.singletonList("string[]"), 'L', List.class),

    /**
     * json数组
     */
    ARRAY(Collections.singletonList("object[]"), 'A', JSONArray.class),

    /**
     * 整形
     */
    INT(Arrays.asList("int", "integer"), 'N', Integer.class),

    /**
     * 长整形
     */
    LONG(Collections.singletonList("long"), 'N', Long.class),

    /**
     * 浮点型
     */
    FLOAT(Collections.singletonList("float"), 'N', Float.class),

    /**
     * 长浮点型
     */
    DOUBLE(Collections.singletonList("double"), 'N', Double.class),

    /**
     * 字符串
     */
    STRING(Collections.singletonList("string"), 'C', String.class),

    /**
     * 浮点型
     */
    BOOLEAN(Collections.singletonList("boolean"), 'B', Boolean.class),

    /**
     * 形状类型
     */
    SHAPE(Collections.singletonList("shape"), 'S', null),

    /**
     * 其他
     */
    OTHERS(Collections.singletonList("others"), 'T', null);

    ValTypeEnum(List<String> typeNames, char shortName, Class<?> type) {
        this.typeNames = typeNames;
        this.shortName = shortName;
        this.type = type;
    }

    private final List<String> typeNames;
    private final char shortName;
    private final Class<?> type;

    public static ValTypeEnum findByTypeName(String typeName) {
        if ("int64".equals(typeName)) {
            return ValTypeEnum.LONG;
        } else if ("int32".equals(typeName)) {
            return ValTypeEnum.INT;
        } else if ("number".equals(typeName)) {
            return ValTypeEnum.INT;
        }
        return Arrays.stream(ValTypeEnum.values()).filter(e -> e.getTypeNames().contains(typeName)).findFirst()
                .orElse(ValTypeEnum.STRING);
    }
}
