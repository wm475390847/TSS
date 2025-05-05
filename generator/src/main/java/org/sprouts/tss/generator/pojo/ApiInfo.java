package org.sprouts.tss.generator.pojo;

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

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口路径
     */
    private String apiPath;

    /**
     * 初始api路径
     */
    private String initialAaiPath;

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
     * 连接方式
     */
    private String contentType;

    /**
     * 请求参数
     */
    private List<ParamProperty> params;


    /**
     * 处理api路径，将{路径参数}改为字符串拼接，拼接样式xxx/xxx/xxx
     *
     * @return 优化后的api路径
     */
    public String getApiPath() {
        String apiPath = this.initialAaiPath.trim();
        if (apiPath.endsWith("}")) {
            apiPath = apiPath.substring(0, apiPath.length() - 1);
        }
        apiPath = apiPath.replaceAll("[{]", "\" + ");
        apiPath = apiPath.replaceAll("[}]", " + \"");
        if (this.initialAaiPath.trim().endsWith("}")) {
            return "\"" + apiPath;
        } else {
            return "\"" + apiPath + "\"";
        }
    }
}