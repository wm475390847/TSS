package com.chaohu.conner.base;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.AbstractHttpRequest;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.MethodEnum;
import lombok.Builder;

/**
 * 7、发送消息 董文强
 *
 * @author wangmin
 * @date Thu May 11 16:07:21 CST 2023
 */
@Builder
public class Q3ReportKlinePostRequest extends AbstractHttpRequest {

    private final String code;

    private final String publTime;

    @Override
    protected Api buildApi() {
        return new Api.Builder()
                .path("/api/dg/finance/visual/zhongzhengbao/q3report/kline")
                .method(MethodEnum.POST)
                .contentType("application/json")
                .requestBody(getCurrentBody())
                .build();
    }

    @Override
    protected Object buildBody() {
        JSONObject object = new JSONObject();
        object.put("code", code);
        object.put("publTime", publTime);
        return object;
    }
}
