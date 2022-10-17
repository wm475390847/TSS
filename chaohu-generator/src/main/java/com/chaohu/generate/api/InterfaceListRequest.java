package com.chaohu.generate.api;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.AbstractHttpRequest;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.MethodEnum;
import com.chaohu.generate.pojo.Constant;
import lombok.Builder;

/**
 * 接口列表
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Builder
public class InterfaceListRequest extends AbstractHttpRequest {
    @Builder.Default
    private final Integer page = 1;
    @Builder.Default
    private final Integer limit = 20;
    private final Integer projectId;

    @Override
    protected Api buildApi() {
        return new Api.Builder()
                .baseUrl(Constant.BASE_URL)
                .innerHost(Constant.HOST)
                .header("Cookie", Constant.TOKEN)
                .path("/api/interface/list")
                .urlParamPart("page", page)
                .urlParamPart("limit", limit)
                .urlParamPart("project_id", projectId)
                .method(MethodEnum.GET)
                .build();
    }

    @Override
    protected JSONObject buildBody() {
        return null;
    }
}
