package com.chaohu.conner.container;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.base.UicLoginApi;
import com.chaohu.conner.config.HttpConfig;
import com.chaohu.conner.http.ResponseLog;
import okhttp3.Response;

/**
 * @author wangmin
 * @date 2022/8/25 14:36
 */
public class AContainer extends AbstractConfigContainer {

    @Override
    public void init() {
        // http请求的配置
        ResponseLog<Response> responseLog = UicLoginApi.builder()
                .account("zhanhaojian@xhzyqa")
                .password("5a263a42712366cd00c54ae8c48909fe")
                .build()
                .execute();
        System.err.println(responseLog.toString());
        JSONObject data = responseLog.getObjResult().getJsonData();
        String token = "_sw_token=" + data.getString("_sw_token");
        System.err.println(token);
        HttpConfig httpConfig = new HttpConfig()
                .baseUrl("https://aiwriter.shuwen.com")
                .ipaddress("116.62.90.214")
                .cookie(token);
        addConfig(httpConfig);
    }
}
