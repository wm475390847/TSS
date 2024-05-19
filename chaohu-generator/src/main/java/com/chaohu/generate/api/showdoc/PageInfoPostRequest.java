package com.chaohu.generate.api.showdoc;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.AbstractHttpRequest;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.MethodEnum;
import com.chaohu.generate.pojo.Constant;
import lombok.Builder;

/**
 * 页面详情接口
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Builder
public class PageInfoPostRequest extends AbstractHttpRequest {
    private final String s;
    private final String pageId;

    @Override
    protected Api buildApi() {
        return new Api.Builder()
                .baseUrl(Constant.BASE_URL)
                .hostname(Constant.HOST)
                .header("Cookie", Constant.TOKEN)
                .contentType("text/html")
                .path("/server/index.php")
                .formDataPart("page_id", pageId)
                .urlParamPart("s", s)
                .method(MethodEnum.HTML)
                .build();
    }


    @Override
    protected JSONObject buildBody() {
        return null;
    }
}
