//package com.chaohu.conner;
//
//import com.chaohu.conner.annotation.Container;
//import com.chaohu.conner.annotation.DingSend;
//import com.chaohu.conner.base.BaseCase;
//import com.chaohu.conner.base.Q3ReportKlinePostRequest;
//import com.chaohu.conner.container.BContainer;
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
//@Container(value = BContainer.class)
//public class TestC extends BaseCase {
//
//    @Test(description = "test_7")
//    @DingSend(phones = {"用例7", "15321527989"}, names = {"废柴"})
//    public void test_7() {
//        ResponseLog<Response> responseLog = Q3ReportKlinePostRequest.builder().code("300371").publTime("2022-10-19 00:00:00").build().execute();
//        log.info(responseLog.toString());
//        ResponseInfo objResult = responseLog.getObjResult();
//        SuperAssert.assertTrue(!objResult.getSuccess(), objResult.getCode(), responseLog);
//    }
//
//    @Test(description = "test_8")
//    public void test_8() {
//        ResponseLog<Response> responseLog = Q3ReportKlinePostRequest.builder().code("300371").publTime("2022-10-19 00:00:00").build().execute();
//        log.info(responseLog.toString());
//        ResponseInfo objResult = responseLog.getObjResult();
//        SuperAssert.assertTrue(!objResult.getSuccess(), objResult.getCode(), responseLog);
//    }
//}
