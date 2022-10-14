//package com.chaohu.conner;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chaohu.conner.annotation.Container;
//import com.chaohu.conner.base.BaseCase;
//import com.chaohu.conner.container.OContainer;
//import com.chaohu.conner.http.Api;
//import com.chaohu.conner.http.MethodEnum;
//import com.chaohu.conner.http.ResponseLog;
//import com.chaohu.conner.http.connector.IConnector;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.Response;
//import org.testng.annotations.Test;
//
///**
// * @author wangmin
// * @date 2022/8/25 13:48
// */
//@Slf4j
//@Container(value = OContainer.class)
//public class TestO extends BaseCase {
//
//    @Test
//    public void test_1() {
//        JSONObject object = new JSONObject();
//        object.put("keyword", "芽菜");
//        Api api = new Api.Builder()
//                .path("/conference/data/config/getTemplateList")
//                .method(MethodEnum.POST)
//                .requestBody(object)
//                .build();
//        IConnector<Response> command = api.getMethodEnum().getConnector();
//        command.api(api).execute();
//        ResponseLog<Response> responseLog = command.getLog();
//        System.err.println(responseLog.toString());
//    }
//
//    @Test
//    public void test_2() {
//        JSONObject object = new JSONObject();
//        object.put("packageId", "wl_a3b10bcfa7b34c9a89d5c1961a29e8f6");
//        Api api = new Api.Builder()
//                .path("/conference/data/config/getFolderList")
//                .method(MethodEnum.POST)
//                .requestBody(object)
//                .build();
//        IConnector<Response> command = api.getMethodEnum().getConnector();
//        command.api(api).execute();
//        ResponseLog<Response> log = command.getLog();
//        System.err.println(log.toString());
//    }
//}
