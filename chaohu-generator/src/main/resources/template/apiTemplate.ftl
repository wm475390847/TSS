package ${packagePath};

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.AbstractHttpRequest;
import com.chaohu.conner.http.MethodEnum;
import lombok.Builder;

/**
 * ${apiName} ${apiAuthor}
 *
 * @author wangmin
 * @date ${date}
 */
@Builder
public class ${className} extends AbstractHttpRequest {
    <#list attrs as attr>
    /**
     * 描述：<#if attr.description??>${attr.description}<#else>null</#if>
     * 是否必填：${attr.required}
     */
    private final ${attr.type} ${attr.value};
    </#list>

    @Override
    protected Api buildApi() {
        <#assign get="GET">
        <#assign multipart="MULTIPART">
        return new Api.Builder()
                .path("${apiPath}")
                .method(MethodEnum.${method})
                .contentType("${contentType}")
        <#if "${method}"?contains(get)>
            <#list attrs as attr>
                .urlParamPart("${attr.key}", ${attr.value})
            </#list>
        <#elseIf "${method}"?contains(multipart)>
                .formDataPart("${attr.key}", ${attr.value})
        <#else>
                .requestBody(getCurrentBody())
        </#if>
                .build();
    }

    @Override
    protected Object buildBody() {
    <#if get=="${method}">
        return null;
    <#elseIf multipart=="${method}">
        return null;
    <#elseIf "${apiPath}"?contains("{")>
        return null;
    <#else>
        JSONObject object = new JSONObject();
        <#list attrs as attr>
        object.put("${attr.key}", ${attr.value});
        </#list>
        return object;
    </#if>
    }
}
