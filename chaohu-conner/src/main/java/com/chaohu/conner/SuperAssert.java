package com.chaohu.conner;

import com.chaohu.conner.http.ResponseLog;
import okhttp3.Response;
import org.testng.Assert;

/**
 * 增强断言
 * 重写assert类
 *
 * @author wangmin
 * @date 2022/1/24 10:40 下午
 */
public class SuperAssert extends Assert {

    public static void assertTrue(boolean condition, String message, String detailMessage) {
        Context.detailMessage = detailMessage;
        Assert.assertTrue(condition, message);
    }

    public static void assertNull(Object object, String message, String detailMessage) {
        Context.detailMessage = detailMessage;
        Assert.assertNull(object, message);
    }

    public static void assertNotNull(Object object, String message, String detailMessage) {
        Context.detailMessage = detailMessage;
        Assert.assertNotNull(object, message);
    }

    public static void assertEquals(long actual, long expected, String message, String detailMessage) {
        Context.detailMessage = detailMessage;
        Assert.assertEquals(actual, expected, message);
    }

    public static void assertEquals(Object actual, Object expected, String message, String detailMessage) {
        Context.detailMessage = detailMessage;
        Assert.assertEquals(actual, expected, message);
    }

    public static void assertNotEquals(Object actual, Object expected, String message, String detailMessage) {
        Context.detailMessage = detailMessage;
        Assert.assertNotEquals(actual, expected, message);
    }

    public static void assertTrue(boolean condition, String message, String failApi, String detailMessage) {
        Context.failApi = failApi;
        Context.detailMessage = detailMessage;
        Assert.assertTrue(condition, message);
    }

    public static void assertNull(Object object, String message, String failApi, String detailMessage) {
        Context.failApi = failApi;
        Context.detailMessage = detailMessage;
        Assert.assertNull(object, message);
    }

    public static void assertNotNull(Object object, String message, String failApi, String detailMessage) {
        Context.failApi = failApi;
        Context.detailMessage = detailMessage;
        Assert.assertNotNull(object, message);
    }

    public static void assertEquals(long actual, long expected, String message, String failApi, String detailMessage) {
        Context.failApi = failApi;
        Context.detailMessage = detailMessage;
        Assert.assertEquals(actual, expected, message);
    }

    public static void assertEquals(Object actual, Object expected, String message, String failApi, String detailMessage) {
        Context.failApi = failApi;
        Context.detailMessage = detailMessage;
        Assert.assertEquals(actual, expected, message);
    }

    public static void assertNotEquals(Object actual, Object expected, String message, String failApi, String detailMessage) {
        Context.failApi = failApi;
        Context.detailMessage = detailMessage;
        Assert.assertNotEquals(actual, expected, message);
    }

    public static void assertTrue(boolean condition, String message, ResponseLog<Response> responseLog) {
        Context.failApi = responseLog.getApi().getUrl();
        Context.requestId = responseLog.getObjResult().getRequestId();
        Assert.assertTrue(condition, message);
    }

    public static void assertNull(Object object, String message, ResponseLog<Response> responseLog) {
        Context.failApi = responseLog.getApi().getUrl();
        Context.requestId = responseLog.getObjResult().getRequestId();
        Assert.assertNull(object, message);
    }

    public static void assertNotNull(Object object, String message, ResponseLog<Response> responseLog) {
        Context.failApi = responseLog.getApi().getUrl();
        Context.requestId = responseLog.getObjResult().getRequestId();
        Assert.assertNotNull(object, message);
    }

    public static void assertEquals(long actual, long expected, String message, ResponseLog<Response> responseLog) {
        Context.failApi = responseLog.getApi().getUrl();
        Context.requestId = responseLog.getObjResult().getRequestId();
        Assert.assertEquals(actual, expected, message);
    }

    public static void assertEquals(Object actual, Object expected, String message, ResponseLog<Response> responseLog) {
        Context.failApi = responseLog.getApi().getUrl();
        Context.requestId = responseLog.getObjResult().getRequestId();
        Assert.assertEquals(actual, expected, message);
    }

    public static void assertNotEquals(Object actual, Object expected, String message, ResponseLog<Response> responseLog) {
        Context.failApi = responseLog.getApi().getUrl();
        Context.requestId = responseLog.getObjResult().getRequestId();
        Assert.assertNotEquals(actual, expected, message);
    }
}
