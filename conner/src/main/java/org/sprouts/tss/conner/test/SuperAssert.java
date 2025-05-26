package org.sprouts.tss.conner;

import org.sprouts.tss.conner.http.response.ResponseLog;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

/**
 * 增强断言
 * 重写assert类
 *
 * @author wangmin
 * @date 2022/1/24 10:40 下午
 */
public class SuperAssert extends Assert {

    public static void assertEquals(Object actual, Object expected, String message, ResponseLog<Response> responseLog) {
        assertEquals(actual, expected, getMessage(message, responseLog));
    }

    public static void assertNotNull(Object object, String message, ResponseLog<Response> responseLog) {
        assertNotNull(object, getMessage(message, responseLog));
    }

    private static String getMessage(String message, ResponseLog<Response> responseLog) {
        String requestId = responseLog.getRequestId();
        if (!StringUtils.isEmpty(requestId)) {
            return "[" + requestId + "]" + message;
        }
        return message;
    }
}
