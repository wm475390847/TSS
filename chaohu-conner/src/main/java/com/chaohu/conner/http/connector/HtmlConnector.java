package com.chaohu.conner.http.connector;

import com.chaohu.conner.http.Api;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;

public class HtmlConnector extends AbstractConnector {
    @Override
    protected void buildRequest(Request.Builder builder, Api api) {
        MediaType mediaType = MediaType.parse(api.getContentType());

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("key1", "value1")
                .add("key2", "value2");
        api.getPartParams().forEach(formBuilder::add);
        builder.post(formBuilder.build());
    }
}
