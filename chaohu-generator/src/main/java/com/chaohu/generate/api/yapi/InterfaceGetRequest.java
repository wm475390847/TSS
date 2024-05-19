package com.chaohu.generate.api.yapi;

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
public class InterfaceGetRequest extends AbstractHttpRequest {
    private final Integer id;

    @Override
    protected Api buildApi() {
        return new Api.Builder()
                .baseUrl(Constant.BASE_URL)
                .hostname(Constant.HOST)
                .header("Cookie", Constant.TOKEN)
                .urlParamPart("id", id)
                .path("api/interface/get")
                .method(MethodEnum.GET)
                .build();
    }

    @Override
    protected JSONObject buildBody() {
        return null;
    }
}
