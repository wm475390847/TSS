package com.chaohu.conner.container;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.base.UicLoginApi;
import com.chaohu.conner.config.HttpConfig;
import com.chaohu.conner.http.ResponseLog;
import com.chaohu.conner.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

/**
 * @author wangmin
 * @date 2022/8/16 17:59
 */
@Slf4j
public class OContainer extends AbstractConfigContainer {

    @Override
    public void init() {
        // http请求的配置
        String password = Md5Util.getMd5("");
        ResponseLog<Response> responseLog = UicLoginApi.builder()
                .password(password)
                .account("")
                .build()
                .execute();
        JSONObject data = responseLog.getObjResult().getJsonData();
        String token = "_sw_token=" + data.getString("_sw_token");
        HttpConfig httpConfig = new HttpConfig()
                .baseUrl("https://test-metaos.shuwen.com")
                .cookie(token);
        addConfig(httpConfig);
    }
}
