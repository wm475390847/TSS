package org.sprouts.tss.generator.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.sprouts.tss.generator.enums.ValTypeEnum;
import org.sprouts.tss.generator.pojo.ApiInfo;
import org.sprouts.tss.generator.pojo.ParamProperty;
import org.sprouts.tss.generator.util.FileUtils;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JsonParse<R> extends LocalBaseApiParse<ApiInfo> {

    public JsonParse(Builder builder) {
        super(builder);
    }

    @Override
    protected List<ApiInfo> getList() {
        // 读取本地的JSON文件
        String fileString = FileUtils.readFileFromSystemToString(getFilePath());
        JSONArray array = JSONArray.parseArray(fileString);
        return array.stream()
                .map(JSONObject.class::cast)
                .map(obj -> {
                    String apiName = obj.getJSONArray("descriptionLines").getString(0);
                    String apiPath = obj.getJSONArray("urls").getString(0);
                    String httpMethod = obj.getString("httpMethod");
                    String author = obj.getString("author");
                    ApiInfo apiInfo = new ApiInfo();
                    apiInfo.setApiSuffix(getSuffix());
                    apiInfo.setApiName(apiName);
                    apiInfo.setApiAuthor(author);
                    apiInfo.setApiPath(apiPath);
                    apiInfo.setInitialAaiPath(apiPath);
                    apiInfo.setMethod(httpMethod.toUpperCase());
                    if (httpMethod.equalsIgnoreCase("POST")) {
                        JSONObject requestBodyJsonSchema = obj.getJSONObject("requestBodyJsonSchema");
                        if (requestBodyJsonSchema != null) {
                            JSONObject properties = requestBodyJsonSchema.getJSONObject("properties");
                            List<ParamProperty> paramProperties = new ArrayList<>();
                            properties.forEach((k, v) -> {
                                ParamProperty paramProperty = new ParamProperty();
                                paramProperty.setKey(k);
                                paramProperty.setValue(k);
                                paramProperty.setRequired("false");
                                JSONObject value = (JSONObject) v;
                                // 解析字段类型
                                String type = ValTypeEnum.findByTypeName(value.getString("type")).getType().getSimpleName();
                                paramProperty.setType(type);
                                JSONObject description = value.getJSONObject("description");
                                JSONArray commentLines = description.getJSONArray("commentLines");
                                JSONArray valids = description.getJSONArray("valids");
                                if (valids != null && !valids.isEmpty()) {
                                    String validStr = JSONArray.toJSONString(valids);
                                    if (validStr.contains("不能为null") || validStr.contains("必须有元素/字符") || validStr.contains("必须有非空格字符")) {
                                        paramProperty.setRequired("true");
                                    }
                                }
                                // 解析字段描述
                                paramProperty.setDescription(commentLines.toJSONString());
                                paramProperties.add(paramProperty);
                            });
                            apiInfo.setParams(paramProperties);
                        }
                    } else if (httpMethod.equalsIgnoreCase("GET")) {
                        log.error("GET请求暂不支持");
                    }
                    return apiInfo;
                }).collect(Collectors.toList());
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends LocalBaseBuilder<JsonParse<ApiInfo>, JsonParse.Builder, ApiInfo> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        protected JsonParse<ApiInfo> buildLocalParse() {
            return new JsonParse<>(this);
        }
    }
}
