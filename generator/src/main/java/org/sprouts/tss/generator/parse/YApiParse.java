package org.sprouts.tss.generator.parse;

import org.sprouts.tss.generator.pojo.ApiInfo;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class YApiParse<R> extends NetBaseApiParse<ApiInfo> {

    public YApiParse(Builder builder) {
        super(builder);
    }

    @Override
    protected List<ApiInfo> getList() {
        return null;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends NetBaseBuilder<YApiParse<ApiInfo>, Builder, ApiInfo> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        protected YApiParse<ApiInfo> buildNetParse() {
            return new YApiParse<>(this);
        }
    }
}
