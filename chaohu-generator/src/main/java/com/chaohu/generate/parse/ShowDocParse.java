package com.chaohu.generate.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.chaohu.conner.http.ResponseLog;
import com.chaohu.generate.api.showdoc.ItemInfoPostRequest;
import com.chaohu.generate.api.showdoc.MyListPostRequest;
import com.chaohu.generate.api.showdoc.PageInfoPostRequest;
import com.chaohu.generate.enums.ValTypeEnum;
import com.chaohu.generate.pojo.ApiInfo;
import com.chaohu.generate.pojo.ParamProperty;
import com.chaohu.generate.util.StringUtil;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Slf4j
public class ShowDocParse extends BaseApiParse<ApiInfo> {

    private static final List<String> pageIds = new ArrayList<>();

    public ShowDocParse(Builder builder) {
        super(builder);
    }

    @Override
    protected List<ApiInfo> getList() {
        // 获取所有项目
        ResponseLog<Response> myListResponse = MyListPostRequest.builder().s("/api/item/myList").build().execute();
        String unescapedResponse = StringEscapeUtils.unescapeJson(myListResponse.getStrResult());
        JSONArray itemList = JSONObject.parseObject(unescapedResponse).getJSONArray("data");
        itemList.stream()
                .map(JSONObject.class::cast)
                .forEach(obj -> {
                    // 获取所有项目的pageId
                    ResponseLog<Response> itemInfoResponse = ItemInfoPostRequest.builder()
                            .s("/api/item/info").itemId(obj.getString("item_id")).build().execute();
                    String s = StringEscapeUtils.unescapeJson(itemInfoResponse.getStrResult());
                    JSONObject itemData = JSONPath.read(s, "data", JSONObject.class);
                    log.info("采集项目: {}", itemData.getString("item_name"));
                    processCatalogs(itemData.getJSONObject("menu"));
                });
        log.info("采集的页面id: {}", pageIds);
        ArrayList<ApiInfo> apiInfos = new ArrayList<>();
        pageIds.forEach(pageId -> {
            log.info("解析页面: {}", pageId);
            ResponseLog<Response> pageInfoResponse = PageInfoPostRequest.builder().pageId(pageId).s("/api/page/info").build().execute();
            JSONObject pageData = JSONPath.read(pageInfoResponse.getStrResult(), "data", JSONObject.class);
            ApiInfo apiInfo = new ApiInfo()
                    .setApiAuthor(StringEscapeUtils.unescapeJson(pageData.getString("author_username")))
                    .setApiName(StringEscapeUtils.unescapeJson(pageData.getString("page_title")));
            String pageContent = StringEscapeUtils.unescapeJson(pageData.getString("page_content"));
            if (StringUtils.isNotEmpty(pageContent.trim()) && pageContent.contains("请求URL")) {
                parseContent(pageContent, apiInfo);
                log.info("解析结果: {}", apiInfo);
                apiInfos.add(apiInfo);
                log.info("-------------");
            }
        });
        return apiInfos;
    }

    public static void parseContent(String input, ApiInfo apiInfo) {
        // 只保留有用的字符
        int indexed = input.indexOf("返");
        input = input.substring(0, indexed);
        Pattern pattern = Pattern.compile("\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|");
        Matcher matcher = pattern.matcher(input);

        // 放入请求路径，需要处理api的路径，显示出可变参数
        String requestURL = extractText(input, "##### 请求URL\\s+- `(.*?)`");
        Optional.ofNullable(requestURL).ifPresent(e -> apiInfo.setInitialAaiPath(e.trim()));

        // 放入请求方法
        String requestMethod = extractText(input, "##### 请求方式\\s+- (.+)");
        Optional.ofNullable(requestMethod).ifPresent(e -> apiInfo.setMethod(e.trim()));

        // 放入请求参数
        List<ParamProperty> paramProperties = new ArrayList<>();
        while (matcher.find()) {
            String param = matcher.group(1).trim();
            // 参数中如果包含横线就转成驼峰并且首字母大写
            param = param.contains("-") || param.contains("_")
                    ? StringUtil.lineToHump(param, true) : param;
            param = param.contains(".") ? param.split("\\.")[0] : param;

            String mandatory = matcher.group(2).trim();
            // 复杂数据结果不做生成，默认为String
            String type = ValTypeEnum.findByTypeName(matcher.group(3).trim()).getType().getSimpleName();
            String paramDesc = matcher.group(4).trim();
            if (StringUtils.isNotEmpty(param)) {
                String finalParam = param;
                boolean exists = paramProperties.stream().anyMatch(p -> p.getValue().equals(finalParam));
                if (!exists) {
                    ParamProperty paramProperty = new ParamProperty()
                            .setKey(param)
                            .setValue(param)
                            .setRequired(mandatory)
                            .setType(type)
                            .setDescription(paramDesc);
                    paramProperties.add(paramProperty);
                }
            }
        }
        // 去除数组中的表格头
        paramProperties.remove(0);
        // 去除数组中的分割线
        paramProperties.remove(0);
        // 去除body中的多余索引
        int index = IntStream.range(0, paramProperties.size())
                .filter(i -> paramProperties.get(i).getKey().equals("参数名"))
                .findFirst()
                .orElse(-1);
        List<ParamProperty> subParamProperties = index == -1 ? paramProperties : new ArrayList<>(paramProperties.subList(0, index));
        apiInfo.setParam(subParamProperties);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<ShowDocParse.Builder, Object, ApiInfo> {

        @Override
        protected IParse<ApiInfo> buildParse() {
            return new ShowDocParse(this);
        }
    }

    private static String extractText(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void processCatalogs(JSONObject obj) {
        getPageId(obj);
        obj.entrySet().stream()
                .filter(e -> e.getKey().contains("catalogs"))
                .map(e -> (JSONArray) e.getValue())
                .filter(e1 -> !e1.isEmpty())
                .flatMap(e1 -> e1.stream().map(JSONObject.class::cast))
                .forEach(ShowDocParse::processCatalogs);
    }

    private static void getPageId(JSONObject obj) {
        JSONArray pages = obj.getJSONArray("pages");
        if (!pages.isEmpty()) {
            pages.stream()
                    .map(JSONObject.class::cast)
                    .forEach(e -> {
                        String pageId = e.getString("page_id");
                        log.info("{} -> {}", e.getString("page_title"), pageId);
                        pageIds.add(pageId);
                    });
        }
    }
}
