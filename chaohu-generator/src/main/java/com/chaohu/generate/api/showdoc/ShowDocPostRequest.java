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
public class ShowDocPostRequest extends AbstractHttpRequest {
    private final String s;
    private String itemId;
    private String pageId;

    @Override
    protected Api buildApi() {
        Api.Builder builder = new Api.Builder()
                .baseUrl(Constant.BASE_URL)
                .hostname(Constant.HOST)
                .header("Cookie", Constant.TOKEN)
                .path("/server/index.php")
                .urlParamPart("s", s)
                .method(MethodEnum.POST);
        if (itemId != null) {
            builder.formDataPart("item_id", itemId);
            builder.formDataPart("default_page_id", "0");
        }
        if (pageId != null) {
            builder.formDataPart("page_id", pageId);
        }
        return builder.build();
    }

    @Override
    protected JSONObject buildBody() {
        return null;
    }
}
