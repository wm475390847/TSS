package com.chaohu.generate.api.showdoc;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.AbstractHttpRequest;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.MethodEnum;
import com.chaohu.generate.pojo.Constant;
import lombok.Builder;

/**
 * 活动列表接口
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Builder
public class ItemUpdateByApiPostRequest extends AbstractHttpRequest {
    private final String s;
    private String apiKey;
    private String apiToken;
    private String pageTitle;
    private String pageContent;

    @Override
    protected Api buildApi() {
        return new Api.Builder()
                .baseUrl(Constant.BASE_URL)
                .hostname(Constant.HOST)
                .header("Cookie", Constant.TOKEN)
                .path("/server/index.php")
                .urlParamPart("s", s)
                .method(MethodEnum.POST)
                .requestBody(getCurrentBody())
                .build();
    }

    @Override
    protected JSONObject buildBody() {
        JSONObject object = new JSONObject();
        object.put("api_key", apiKey);
        object.put("api_token", apiToken);
        object.put("page_title", pageTitle);
        object.put("page_content", pageContent);
        return object;
    }
}
