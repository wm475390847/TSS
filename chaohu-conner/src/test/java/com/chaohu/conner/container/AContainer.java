package com.chaohu.conner.container;

import com.chaohu.conner.base.Message;
import com.chaohu.conner.config.DingDingConfig;
import com.chaohu.conner.config.HttpConfig;

/**
 * @author wangmin
 * @date 2022/8/25 14:36
 */
public class AContainer extends AbstractConfigContainer {

    @Override
    public void init() {
        // http请求的配置
//        ResponseLog<Response> responseLog = UicLoginApi.builder()
//                .account("")
//                .password("")
//                .build()
//                .execute();
//        JSONObject data = responseLog.getObjResult().getJsonData();
//        String token = "_sw_token=" + data.getString("_sw_token");
        HttpConfig httpConfig = new HttpConfig()
                .baseUrl("https://aiwriter.shuwen.com")
//                .ipaddress("116.62.90.214")
                ;
        DingDingConfig dingConfig = new DingDingConfig()
                .messageFormat(new Message())
                .isAtAll(true)
                .keyword("报警")
                .phones("用例1", "用例2")
                .webhook("https://oapi.dingtalk.com/robot/send?access_token=02cccc359c45b0fb4913d1f4cbe4354f51a679be711c969c8969c7adb57cc937");
        addConfig(httpConfig);
        addConfig(dingConfig);
    }
}
