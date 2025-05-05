package org.sprouts.tss.generator;

import org.sprouts.tss.generator.core.IGenerator;
import org.sprouts.tss.generator.core.JsonGenerator;
import org.sprouts.tss.generator.core.ShowDocGenerator;
import org.sprouts.tss.generator.parse.JsonParse;
import org.sprouts.tss.generator.parse.ShowDocParse;
import org.sprouts.tss.generator.pojo.ApiInfo;
import org.testng.annotations.Test;

public class TestGenerator {
    @Test
    public void test() {
        String cookie = "cookie_token=d91860ab4610429d471f447ebfe4c2d62162b9edbc531a85bce2ab041539eb8f";
        ShowDocParse<ApiInfo> parse = new ShowDocParse.Builder()
                .suffix("Request")
                .token(cookie)
                .baseUrl("http://codewave-api.netease.com")
                .build();
        IGenerator generator = new ShowDocGenerator.Builder()
                .parse(parse)
                .parentPathIndex(3)
                .templatePath("src/main/resources/template")
                .templateName("apiTemplate.ftl")
                .outputPath("src/main/java/com/netease/codewave/quality/tmp")
                .build();
        generator.load().execute();
    }

    @Test
    public void test1() {
        JsonParse<ApiInfo> jsonParse = new JsonParse.Builder()
                .suffix("Request")
                .filePath("/Users/wangmin/Downloads/开放平台凭证管理.json")
                .build();
        IGenerator generator = new JsonGenerator.Builder()
                .parse(jsonParse)
                .parentPathIndex(3)
                .templatePath("src/main/resources/template")
                .templateName("apiTemplate.ftl")
                .outputPath("src/main/java/com/netease/codewave/quality/tmp")
                .build();
        generator.load().execute();
    }
}
