package com.chaohu.conner.base;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.MethodEnum;
import com.chaohu.conner.http.AbstractHttpRequest;
import lombok.Builder;

/**
 * uic平台登陆
 *
 * @author wangmin
 * @date 2021/9/28 2:36 下午
 */
@Builder
public class UicLoginApi extends AbstractHttpRequest {
    private final String account;
    private final String password;

    @Override
    protected Api buildApi() {
        return new Api.Builder()
                .baseUrl("https://test.account.shuwen.com")
                .path("/api/uic/login/pw")
                .requestBody(getCurrentBody())
                .method(MethodEnum.POST)
                .build();
    }

    @Override
    protected Object buildBody() {
        JSONObject object = new JSONObject();
        object.put("passwordx", password);
        object.put("credential", account);
        return object;
    }
}