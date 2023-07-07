package com.chaohu.conner.container;

import com.alibaba.fastjson.JSONObject;
import com.chaohu.conner.base.Message;
import com.chaohu.conner.base.UicLoginApi;
import com.chaohu.conner.config.DingDingConfig;
import com.chaohu.conner.config.HttpConfig;
import com.chaohu.conner.http.ResponseLog;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

/**
 * @author wangmin
 * @date 2022/8/16 17:59
 */
@Slf4j
public class BContainer extends AbstractConfigContainer {

    @Override
    public void init() {
        // http请求的配置
        ResponseLog<Response> responseLog = UicLoginApi.builder()
                .password("5a263a42712366cd00c54ae8c48909fe")
                .account("zhanhaojian@xhzyqa")
                .build()
                .execute();
        JSONObject data = responseLog.getObjResult().getJsonData();
        String token = "_sw_token=" + data.getString("_sw_token");
        HttpConfig httpConfig = new HttpConfig()
                .baseUrl("https://aiwriter.shuwen.com")
                .ipaddress("116.62.90.214")
                .cookie(token);
        DingDingConfig dingConfig = new DingDingConfig()
                .messageFormat(new Message())
                .keyword("报警")
                .phones("用例5", "用例6")
                .webhook("https://oapi.dingtalk.com/robot/send?access_token=02cccc359c45b0fb4913d1f4cbe4354f51a679be711c969c8969c7adb57cc937");
        addConfig(httpConfig);
        addConfig(dingConfig);
    }
}
