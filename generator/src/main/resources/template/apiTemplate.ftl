package ${packagePath};

<#if attrs?? && (attrs?size > 0)>
import com.alibaba.fastjson.JSONObject;
</#if>
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.AbstractHttpRequest;
import com.chaohu.conner.http.MethodEnum;
import lombok.Builder;

/**
 * ${apiName}
 * 接口开发者：${apiAuthor}
 *
 * @author wangmin
 * @date ${date}
 */
@Builder
public class ${className} extends AbstractHttpRequest {
    <#if attrs?? && (attrs?size > 0)>
    <#list attrs as attr>

    /**
     * 描述：<#if attr.description??>${attr.description}<#else>null</#if>
     * 是否必填：${attr.required}
     */
    private final ${attr.type} ${attr.value};
    </#list>
    </#if>

    @Override
    protected Api buildApi() {
        <#assign get="GET">
        <#assign multipart="MULTIPART">
        return new Api.Builder()
                .path(${apiPath})
        <#if method??>
                .method(MethodEnum.${method})
        </#if>
        <#if contentType??>
                .contentType("${contentType}")
        </#if>
        <#if method??>
            <#if method?contains(get)>
                <#if attrs?? && (attrs?size > 0)>
                <#list attrs as attr>
                .urlParamPart("${attr.key}", ${attr.value})
                </#list>
                </#if>
            <#elseIf method?contains(multipart)>
                <#if attrs?? && (attrs?size > 0)>
                <#list attrs as attr>
                .formDataPart("${attr.key}", ${attr.value})
                </#list>
                </#if>
            <#else>
                .requestBody(getCurrentBody())
            </#if>
        </#if>
                .build();
    }

    @Override
    protected Object buildBody() {
    <#if method??>
        <#if get==method>
        return null;
        <#elseIf multipart==method>
        return null;
        <#elseIf apiPath?contains("{")>
        return null;
        <#elseIf !attrs?? || attrs?size == 0>
        return null;
        <#else>
        JSONObject object = new JSONObject();
            <#list attrs as attr>
        object.put("${attr.key}", ${attr.value});
            </#list>
        return object;
        </#if>
    <#else>
        return null;
    </#if>
    }
}
