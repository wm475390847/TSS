//package com.chaohu.conner;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chaohu.conner.annotation.Container;
//import com.chaohu.conner.base.BaseCase;
//import com.chaohu.conner.base.ConfigGetAppTopicsPostApi;
//import com.chaohu.conner.base.Q3ReportKlinePostRequest;
//import com.chaohu.conner.container.AContainer;
//import com.chaohu.conner.http.Api;
//import com.chaohu.conner.http.MethodEnum;
//import com.chaohu.conner.http.ResponseLog;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.Response;
//import org.testng.annotations.Test;
//
///**
// * @author wangmin
// * @date 2022/8/25 13:48
// */
//@Slf4j
//@Container(value = AContainer.class)
//public class TestA extends BaseCase {
//
//    @Test
//    public void test_3() {
//        ResponseLog<Response> responseLog = ConfigGetAppTopicsPostApi.builder()
//                .miceId("wl_a3b10bcfa7b34c9a89d5c1961a29e8f6").build().execute();
//        System.err.println(responseLog.toString());
//    }
//
//    @Test
//    public void test_4() {
//        ResponseLog<Response> responseLog = ConfigGetAppTopicsPostApi.builder()
//                .miceId("wl_a3b10bcfa7b34c9a89d5c1961a29e8f6").build().execute();
//        System.err.println(responseLog.toString());
//    }
//
//    @Test
//    public void test_5() {
//        ResponseLog<Response> responseLog = Q3ReportKlinePostRequest.builder().code("300371").publTime("2022-10-19 00:00:00").build().execute();
//        log.info(responseLog.toString());
//    }
//
//    @Test
//    public void test_6() {
//        JSONObject object = new JSONObject();
//        object.put("code", "300371");
//        object.put("publTime", "2022-10-19 00:00:00");
//        ResponseLog<Response> responseLog = new Api.Builder()
//                .path("/api/dg/finance/visual/zhongzhengbao/q3report/kline")
//                .method(MethodEnum.POST)
//                .requestBody(object)
//                .build()
////                .setHttpConfig(new HttpConfig().ipaddress("116.62.90.214"))
//                .execute();
//        log.info(responseLog.toString());
//    }
//}
