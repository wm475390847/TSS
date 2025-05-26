package org.sprouts.tss.conner.http.client;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author wangmin
 * @date 2022/5/17 13:05
 */
public class PostConnector extends AbstractConnector {
    @Override
    protected void buildRequest(Request.Builder builder, Api api) {
        // 解析api类中的contentType
        MediaType mediaType = MediaType.parse(api.getContentType());

        // 发起post请求
        Object bodyContent = api.getRequestBody();
        RequestBody requestBody = bodyContent == null ? RequestBody.create(mediaType, new byte[0])
                : RequestBody.create(mediaType, bodyContent.toString());
        builder.post(requestBody);
    }
}
