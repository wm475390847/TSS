//package com.chaohu.conner;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chaohu.conner.annotation.Collector;
//import com.chaohu.conner.annotation.Container;
//import com.chaohu.conner.annotation.DingSend;
//import com.chaohu.conner.base.BaseCase;
//import com.chaohu.conner.base.CaseCollector;
//import com.chaohu.conner.base.Q3ReportKlinePostRequest;
//import com.chaohu.conner.container.AContainer;
//import com.chaohu.conner.http.Api;
//import com.chaohu.conner.http.MethodEnum;
//import com.chaohu.conner.http.ResponseInfo;
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
//@Collector(value = CaseCollector.class)
//@Container(value = AContainer.class)
//public class TestA extends BaseCase {
//
//    @Test(description = "test_1")
//    public void test_1() {
//        ResponseLog<Response> responseLog = Q3ReportKlinePostRequest.builder().code("300371").publTime("2022-10-19 00:00:00").build().execute();
//        log.info(responseLog.toString());
//        ResponseInfo objResult = responseLog.getObjResult();
//        SuperAssert.assertTrue(objResult.getSuccess(), objResult.getCode(), responseLog);
//    }
//
//    @Test(description = "test_2")
//    @DingSend(phones = {"用例2"}, names = {"芽菜"})
//    public void test_2() {
//        JSONObject object = new JSONObject();
//        object.put("code", "300371");
//        object.put("publTime", "2022-10-19 00:00:00");
//        Api api = new Api.Builder()
//                .path("/api/dg/finance/visual/zhongzhengbao/q3report/kline")
//                .method(MethodEnum.POST)
//                .requestBody(object)
//                .build()
//                .pure();
//        Boolean pure = api.getPure();
//        System.err.println(pure);
//    }
//}
