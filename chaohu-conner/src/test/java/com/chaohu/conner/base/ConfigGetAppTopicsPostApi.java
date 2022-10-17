package com.chaohu.conner.base;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.http.AbstractHttpRequest;
import com.chaohu.conner.http.Api;
import com.chaohu.conner.http.MethodEnum;
import lombok.Builder;

/**
 * app文件夹配置获取列表 黄振方
 *
 * @author wangmin
 * @date Wed Aug 24 14:19:36 CST 2022
 */
@Builder
public class ConfigGetAppTopicsPostApi extends AbstractHttpRequest {
    /**
     * 描述：活动ID
     * 是否必填：true
     */
    private final String miceId;

    @Override
    protected Api buildApi() {
        return new Api.Builder()
                .path("/conference/data/config/getAppTopics")
                .method(MethodEnum.POST)
                .contentType("application/json")
                .requestBody(getCurrentBody())
                .build();
    }

    @Override
    protected Object buildBody() {
        JSONObject object = new JSONObject();
        object.put("miceId", miceId);
        return object;
    }
}
