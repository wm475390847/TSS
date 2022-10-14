package com.chaohu.conner;

import com.chaohu.conner.annotation.Container;
import com.chaohu.conner.base.BaseCase;
import com.chaohu.conner.base.ConfigGetAppTopicsPostApi;
import com.chaohu.conner.container.AContainer;
import com.chaohu.conner.http.ResponseLog;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.testng.annotations.Test;

/**
 * @author wangmin
 * @date 2022/8/25 13:48
 */
@Slf4j
@Container(value = AContainer.class)
public class TestA extends BaseCase {

    @Test
    public void test_3() {
        ResponseLog<Response> responseLog = ConfigGetAppTopicsPostApi.builder()
                .miceId("wl_a3b10bcfa7b34c9a89d5c1961a29e8f6").build().execute();
        System.err.println(responseLog.toString());
    }

    @Test
    public void test_4() {
        ResponseLog<Response> responseLog = ConfigGetAppTopicsPostApi.builder()
                .miceId("wl_a3b10bcfa7b34c9a89d5c1961a29e8f6").build().execute();
        System.err.println(responseLog.toString());
    }
}
