package org.sprouts.tss.generator.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.sprouts.tss.generator.enums.ValTypeEnum;
import org.sprouts.tss.generator.pojo.ApiInfo;
import org.sprouts.tss.generator.pojo.Constant;
import org.sprouts.tss.generator.pojo.ParamProperty;
import org.sprouts.tss.generator.util.StringUtils;
import io.restassured.response.Response;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;


@Slf4j
public class ShowDocParse<R> extends NetBaseApiParse<ApiInfo> {

    private static final List<String> pageIds = new ArrayList<>();

    public ShowDocParse(Builder builder) {
        super(builder);
    }

    @Override
    protected List<ApiInfo> getList() {
        // 获取所有项目
        Response myListResponse = given()
                .baseUri(Constant.BASE_URL)
                .basePath("/server/index.php")
                .queryParam("s", "/api/item/myList")
                .formParam("default_page_id", "0")
                .header("Cookie", Constant.TOKEN)
                .post()
                .then()
                .extract()
                .response();
        String unescapedResponse = StringEscapeUtils.unescapeJson(myListResponse.body().asString());
        JSONArray itemList = JSONObject.parseObject(unescapedResponse).getJSONArray("data");
        itemList.stream()
                .map(JSONObject.class::cast)
                .forEach(obj -> {
                    Response infoResponse = given()
                            .baseUri(Constant.BASE_URL)
                            .basePath("/server/index.php")
                            .queryParam("s", "/api/item/info")
                            .formParam("item_id", obj.getString("item_id"))
                            .formParam("default_page_id", "0")
                            .header("Cookie", Constant.TOKEN)
                            .post()
                            .then()
                            .extract()
                            .response();
                    String s = StringEscapeUtils.unescapeJson(infoResponse.body().asString());
                    JSONObject itemData = JSONPath.read(s, "data", JSONObject.class);
                    log.info("采集项目: {}", itemData.getString("item_name"));
                    processCatalogs(itemData.getJSONObject("menu"));
                });
        log.info("采集的页面id: {}", pageIds);
        ArrayList<ApiInfo> apiInfos = new ArrayList<>();
        pageIds.forEach(pageId -> {
            log.info("解析页面: {}", pageId);
            Response pageInfoResponse = given()
                    .baseUri(Constant.BASE_URL)
                    .basePath("/server/index.php")
                    .queryParam("s", "/api/page/info")
                    .formParam("page_id", pageId)
                    .header("Cookie", Constant.TOKEN)
                    .post()
                    .then()
                    .extract()
                    .response();
            JSONObject pageData = JSONPath.read(pageInfoResponse.body().asString(), "data", JSONObject.class);
            ApiInfo apiInfo = new ApiInfo()
                    .setApiSuffix(getSuffix())
                    .setApiAuthor(StringEscapeUtils.unescapeJson(pageData.getString("author_username")))
                    .setApiName(StringEscapeUtils.unescapeJson(pageData.getString("page_title")));
            String pageContent = StringEscapeUtils.unescapeJson(pageData.getString("page_content"));
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(pageContent.trim()) && pageContent.contains("请求URL")) {
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
                    ? StringUtils.lineToHump(param, true) : param;
            param = param.contains(".") ? param.split("\\.")[0] : param;

            String mandatory = matcher.group(2).trim();
            // 复杂数据结果不做生成，默认为String
            String type = ValTypeEnum.findByTypeName(matcher.group(3).trim()).getType().getSimpleName();
            String paramDesc = matcher.group(4).trim();
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(param)) {
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
        if (!paramProperties.isEmpty()) {
            paramProperties.remove(0);
        }
        // 去除数组中的分割线
        if (!paramProperties.isEmpty()) {
            paramProperties.remove(0);
        }
        // 去除body中的多余索引
        int index = IntStream.range(0, paramProperties.size())
                .filter(i -> paramProperties.get(i).getKey().equals("参数名"))
                .findFirst()
                .orElse(-1);
        List<ParamProperty> subParamProperties = index == -1 ? paramProperties : new ArrayList<>(paramProperties.subList(0, index));
        apiInfo.setParams(subParamProperties);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends NetBaseBuilder<ShowDocParse<ApiInfo>, Builder, ApiInfo> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        protected ShowDocParse<ApiInfo> buildNetParse() {
            return new ShowDocParse<>(this);
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
