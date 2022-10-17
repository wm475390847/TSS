package com.chaohu.generate.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * api_info
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Data
@Accessors(chain = true)
public class ApiInfo implements Serializable {

    private Integer id;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口列表
     */
    private String apiPath;

    /**
     * 接口后缀
     */
    private String apiSuffix;

    /**
     * 接口基本路径
     */
    private String basePath;

    /**
     * 接口作者
     */
    private String apiAuthor;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 接口描述
     */
    private String contentType;

    /**
     * json字段
     */
    private List<ParamProperty> param;

    /**
     * 分组id
     */
    private Integer groupId;

    /**
     * 是否被覆盖
     */
    private Boolean isCover;

    /**
     * yapi对应每一行的id
     */
    private Integer rowId;

    /**
     * 项目id
     */
    private Integer projectId;

    private static final long serialVersionUID = 1L;
}