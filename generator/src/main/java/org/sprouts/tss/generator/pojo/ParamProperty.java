package org.sprouts.tss.generator.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 参数属性
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Data
@Accessors(chain = true)
public class ParamProperty implements Serializable {

    /**
     * 字段
     */
    private String key;

    /**
     * 字段值，理论上与key一致，但可能存在特殊情况
     */
    private String value;

    /**
     * 是否必填
     */
    private String required;

    /**
     * 字段描述
     */
    private String description;

    /**
     * 字段类型
     */
    private String type;
}
