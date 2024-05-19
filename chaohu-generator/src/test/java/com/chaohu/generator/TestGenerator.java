package com.chaohu.generator;

import com.chaohu.generate.core.IGenerator;
import com.chaohu.generate.core.ShowDocGenerator;
import com.chaohu.generate.parse.IParse;
import com.chaohu.generate.parse.ShowDocParse;
import com.chaohu.generate.pojo.ApiInfo;
import org.testng.annotations.Test;

public class TestGenerator {

    @Test
    public void test() {
        String cookie = "cookie_token=d91860ab4610429d471f447ebfe4c2d62162b9edbc531a85bce2ab041539eb8f";
        IParse<ApiInfo> parse = new ShowDocParse.Builder()
                .token(cookie)
                .baseUrl("http://codewave-api.netease.com")
                .build();
        IGenerator generator = new ShowDocGenerator.Builder()
                .parse(parse)
                .suffix("Request")
                .parentIndex(3)
                .templateName("apiTemplate.ftl")
                .templatePath("src/main/resources/template")
                .outputPath("src/main/java/com/chaohu/generate/tmp")
                .build();
        generator.load();
        generator.execute();
    }
}
