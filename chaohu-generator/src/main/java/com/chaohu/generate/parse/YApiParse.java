package com.chaohu.generate.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.ResponseInfo;
import com.chaohu.conner.http.ResponseLog;
import com.chaohu.generate.api.InterfaceGetRequest;
import com.chaohu.generate.api.InterfaceListRequest;
import com.chaohu.generate.api.InterfaceListCatRequest;
import com.chaohu.generate.api.ProjectGetRequest;
import com.chaohu.generate.enums.ValTypeEnum;
import com.chaohu.generate.execption.GeneratorException;
import com.chaohu.generate.pojo.ApiInfo;
import com.chaohu.generate.pojo.ParamProperty;
import com.chaohu.generate.util.StringUtil;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * yapi的解析类
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Slf4j
public class YApiParse extends BaseApiParse<ApiInfo> {
    private static final String SIGN = "/";
    private final Integer projectId;
    private final Integer[] catIds;

    public YApiParse(Builder builder) {
        super(builder);
        this.catIds = builder.catIds;
        this.projectId = builder.projectId;
    }

    @Override
    public List<ApiInfo> getList() {
        String basePath = getBasePath() == null ? parseBasePath(projectId) == null ? "" : parseBasePath(projectId) : getBasePath();
        JSONArray apiInfo = projectId == null ? getApiInfoByCatId(catIds) : getApiInfoByProjectId(projectId);
        return apiInfo.stream().map(e -> (JSONObject) e).map(e -> splitData(e, basePath)).collect(Collectors.toList());
    }

    /**
     * 通过产品id获取api信息
     *
     * @param projectId 产品id
     * @return api信息集合
     */
    private JSONArray getApiInfoByProjectId(Integer projectId) {
        Optional.ofNullable(projectId).orElseThrow(() -> new GeneratorException("projectId为空"));
        JSONArray array = new JSONArray();
        ResponseLog<Response> responseLog = InterfaceListRequest.builder().projectId(projectId).build().execute();
        ResponseInfo responseInfo = responseLog.getObjResult();
        checkResponse(responseInfo);
        Integer total = responseInfo.getJsonData().getInteger("total");
        for (int i = 1; i <= total; i++) {
            responseLog = InterfaceListRequest.builder().projectId(projectId).page(i).build().execute();
            JSONArray list = responseLog.getObjResult().getJsonData().getJSONArray("list");
            array.addAll(list);
        }
        return array;
    }

    /**
     * 通过catId获取api信息
     *
     * @param catIds catIds
     * @return api信息集合
     */
    private JSONArray getApiInfoByCatId(Integer[] catIds) {
        Optional.ofNullable(catIds).orElseThrow(() -> new GeneratorException("catIds不能为空"));
        JSONArray array = new JSONArray();
        Arrays.stream(catIds).forEach(catId -> {
            ResponseLog<Response> responseLog = InterfaceListCatRequest.builder().catId(catId).build().execute();
            ResponseInfo responseInfo = responseLog.getObjResult();
            checkResponse(responseInfo);
            Integer total = responseInfo.getJsonData().getInteger("total");
            for (int i = 1; i <= total; i++) {
                responseLog = InterfaceListCatRequest.builder().catId(catId).page(i).build().execute();
                JSONArray list = responseLog.getObjResult().getJsonData().getJSONArray("list");
                array.addAll(list);
            }
        });
        return array;
    }

    /**
     * 分解数据
     *
     * @param object   数据
     * @param basePath 基础路径
     * @return api信息
     */
    private ApiInfo splitData(JSONObject object, String basePath) {
        ApiInfo apiInfo = new ApiInfo();
        try {
            Integer rowId = object.getInteger("_id");

            // 接口详情
            JSONObject data = InterfaceGetRequest
                    .builder()
                    .id(rowId)
                    .build()
                    .execute()
                    .getObjResult()
                    .getJsonData();

            String path = data.getString("path");
            path = basePath == null ? path : basePath + path;
            path = path.startsWith(SIGN) ? path : SIGN + path;
            path = path.endsWith(SIGN) ? path.substring(0, path.length() - 1) : path;

            // 由于开发在yapi写的不规范，所以加一个判断逻辑
            path = path.contains(".com") ? path.split(".com")[1] : path;

            String method = data.getString("method");
            String title = data.getString("title");
            String username = data.getString("username");

            JSONArray reqHeaders = data.getJSONArray("req_headers");

            String contentType;
            if (reqHeaders.size() != 0) {
                // post请求的header是有东西的
                JSONObject header = reqHeaders
                        .stream()
                        .map(req -> (JSONObject) req)
                        .filter(req -> "Content-Type".equals(req.getString("name")))
                        .findFirst()
                        .orElse(null);
                Optional.ofNullable(header).orElseThrow(() -> new GeneratorException("header 为空"));
                contentType = header.getString("value");
            } else {
                // get请求的header默认为get
                contentType = "application/json";
            }

            List<ParamProperty> param;
            JSONObject reqBodyOther = JSONObject.parseObject(data.getString("req_body_other"));
            JSONArray reqQuery = data.getJSONArray("req_query");

            if (reqBodyOther != null) {
                // post请求的参数字段
                param = parsePropertiesAndRequired(reqBodyOther.getJSONObject("properties"),
                        reqBodyOther.getJSONArray("required"));
            } else if (reqQuery.size() != 0) {
                // get请求的参数字段
                param = parseReqQuery(reqQuery);
            } else {
                JSONArray reqParams = data.getJSONArray("req_params");
                param = parseReqQuery(reqParams);
            }
            apiInfo.setRowId(rowId)
                    .setApiPath(path)
                    .setApiName(title)
                    .setMethod(method)
                    .setApiAuthor(username)
                    .setContentType(contentType)
                    .setIsCover(false)
                    .setParam(param);
        } catch (Exception e) {
            log.error("处理数据错误: {}", e.getMessage());
        }
        return apiInfo;
    }

    /**
     * 解析基础路径
     *
     * @param projectId 项目id
     * @return 路径
     */
    private String parseBasePath(Integer projectId) {
        if (projectId == null) {
            return null;
        }
        ResponseInfo responseInfo = ProjectGetRequest
                .builder()
                .projectId(projectId)
                .build()
                .execute()
                .getObjResult();
        checkResponse(responseInfo);
        return responseInfo.getJsonData().getString("basepath");
    }

    /**
     * 解析properties和required
     *
     * @param properties 属性集合
     * @param required   必须标记
     * @return list
     */
    private List<ParamProperty> parsePropertiesAndRequired(JSONObject properties, JSONArray required) {
        List<ParamProperty> list = new LinkedList<>();
        if (properties == null) {
            return list;
        }
        properties.forEach((key, value) -> {
            ParamProperty paramProperty = new ParamProperty();
            String d = required != null ? required.contains(key) ? "true" : "false" : "false";
            JSONObject propertyObj = (JSONObject) value;
            String type = ValTypeEnum.findByTypeName(propertyObj.getString("type")).getType().getSimpleName();
            String filedValue = key.contains("-") || key.contains("_")
                    ? StringUtil.lineToHump(key, true) : key;
            paramProperty.setDescription(propertyObj.getString("description"))
                    .setType(type)
                    .setKey(key)
                    .setValue(filedValue)
                    .setRequired(d);
            list.add(paramProperty);
        });
        return list;
    }

    /**
     * 解析reqQuery
     *
     * @param reqQuery 请求体具体为url？后面的参数
     * @return list
     */
    private List<ParamProperty> parseReqQuery(JSONArray reqQuery) {
        List<ParamProperty> list = new LinkedList<>();
        reqQuery.stream().map(e -> (JSONObject) e).forEach(e -> {
            ParamProperty paramProperty = new ParamProperty();
            String key = e.getString("name");
            String filedValue = key.contains("-") || key.contains("_")
                    ? StringUtil.lineToHump(key, true) : key;
            String r = e.getString("required");
            String required = r == null ? "true" : "1".equals(r) ? "true" : "false";
            String type = ValTypeEnum.findByTypeName("string").getType().getSimpleName();
            paramProperty.setKey(key)
                    .setValue(filedValue)
                    .setType(type)
                    .setRequired(required)
                    .setDescription(e.getString("desc"));
            list.add(paramProperty);
        });
        return list;
    }

    /**
     * 校验请求是否成功
     *
     * @param responseInfo 返回值信息
     */
    private void checkResponse(ResponseInfo responseInfo) {
        Optional.ofNullable(responseInfo).filter(e -> !"请登录...".equals(e.getErrMsg()))
                .orElseThrow(() -> new GeneratorException("token过期请重新设置"));
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<Builder, Object, ApiInfo> {
        private Integer projectId;
        private Integer[] catIds;

        public Builder catIds(Integer... catIds) {
            this.catIds = catIds.clone();
            return this;
        }

        @Override
        protected IParse<ApiInfo> buildParse() {
            return new YApiParse(this);
        }
    }
}
