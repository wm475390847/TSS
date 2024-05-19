package com.chaohu.generate.api.showdoc;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.AbstractHttpRequest;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.MethodEnum;
import com.chaohu.generate.pojo.Constant;
import lombok.Builder;

/**
 * 项目详情接口
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Builder
public class ItemInfoPostRequest extends AbstractHttpRequest {
    private final String s;
    private String itemId;

    @Override
    protected Api buildApi() {
        return new Api.Builder()
                .baseUrl(Constant.BASE_URL)
                .hostname(Constant.HOST)
                .header("Cookie", Constant.TOKEN)
                .contentType("text/html")
                .path("/server/index.php")
                .formDataPart("item_id", itemId)
                .formDataPart("default_page_id", "0")
                .urlParamPart("s", s)
                .method(MethodEnum.HTML)
                .build();
    }


    @Override
    protected JSONObject buildBody() {
        return null;
    }
}
