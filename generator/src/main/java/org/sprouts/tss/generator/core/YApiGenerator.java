package org.sprouts.tss.generator.core;

import org.sprouts.tss.generator.enums.FileFormatEnum;
import org.sprouts.tss.generator.parse.YApiParse;
import org.sprouts.tss.generator.pojo.ApiInfo;
import org.sprouts.tss.generator.pojo.FtlParam;
import org.sprouts.tss.generator.pojo.Structure;
import org.sprouts.tss.generator.util.ParseUtils;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Slf4j
public class YApiGenerator extends BaseGenerator<ApiInfo> {
    private final YApiParse<ApiInfo> parse;

    public YApiGenerator(Builder builder) {
        super(builder);
        this.parse = builder.parse;
    }

    @Override
    public IGenerator load() {
        List<ApiInfo> list = parse.execute();
        for (int i = 0; i < list.size(); i++) {
            ApiInfo apiInfo = list.get(i);
            String outputPath = ParseUtils.getOutputPath(getOutputPath());
            String packageName = ParseUtils.getPackageName(getOutputPath());
            String className = ParseUtils.getClassName(apiInfo.getApiPath(), apiInfo.getMethod(), apiInfo.getApiSuffix());

            //生成模版属性
            FtlParam ftlParam = initFtlParam(apiInfo, className, packageName);

            //生成自动生成属性
            Structure structure = new Structure()
                    .setFtlParam(ftlParam)
                    .setClassName(className)
                    .setOutputPath(outputPath)
                    .setFileSuffix(FileFormatEnum.JAVA.getSuffix());

            if (ftlParam.getApiPath() != null) {
                log.info("structure: {}", structure);
                structureMap.put(i, structure);
            }
        }
        return this;
    }

    /**
     * 初始化模版属性
     *
     * @param apiInfo 接口信息
     * @return 模版属性
     */
    public FtlParam initFtlParam(ApiInfo apiInfo, String className, String packageName) {
        return new FtlParam()
                .setMethod(apiInfo.getMethod())
                .setContentType(apiInfo.getContentType())
                .setApiName(apiInfo.getApiName())
                .setApiAuthor(apiInfo.getApiAuthor())
                .setApiPath(apiInfo.getApiPath())
                .setAttrs(apiInfo.getParams())
                .setClassName(className)
                .setPackagePath(packageName)
                .setDate(new Date().toString());
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<Builder, ApiInfo> {
        private YApiParse<ApiInfo> parse;

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        protected IGenerator buildMarker() {
            return new YApiGenerator(this);
        }
    }
}
