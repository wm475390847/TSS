package com.chaohu.generate.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Data
@Accessors(chain = true)
public class FtlParam implements Serializable {

    /**
     * 包路径
     */
    public String packagePath;

    /**
     * 类名
     */
    public String className;

    /**
     *
     */
    public String apiPath;

    /**
     * 生成时间
     */
    public String date;

    /**
     * 请求方法
     */
    public String method;

    /**
     * 内容类型
     */
    public String contentType;

    /**
     * 接口标题
     */
    public String apiName;

    /**
     * 接口作者
     */
    public String apiAuthor;

    /**
     * 是否有array的请求参数，默认false
     */
    public Boolean hasArrayParam;

    /**
     * 内部属性
     */
    public List<ParamProperty> attrs;
}
