package org.sprouts.tss.generator.core;

import org.sprouts.tss.generator.enums.FileFormatEnum;
import org.sprouts.tss.generator.parse.ShowDocParse;
import org.sprouts.tss.generator.pojo.ApiInfo;
import org.sprouts.tss.generator.pojo.FtlParam;
import org.sprouts.tss.generator.pojo.Structure;
import org.sprouts.tss.generator.util.ParseUtils;
import org.sprouts.tss.generator.util.StringUtils;
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
public class ShowDocGenerator extends BaseGenerator<ApiInfo> {
    private final ShowDocParse<ApiInfo> parse;

    public ShowDocGenerator(Builder builder) {
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
            String parentPath = null;
            String initialAaiPath = apiInfo.getInitialAaiPath();
            if (initialAaiPath == null) {
                continue;
            }
            if (initialAaiPath.contains("/")) {
                String[] split = initialAaiPath.split("/");
                parentPath = split.length <= getParentPathIndex() ? split[1] : split[getParentPathIndex()];
                parentPath = parentPath.contains("-") || parentPath.contains("_") ? StringUtils.lineToHump(parentPath, false) : parentPath;
                parentPath = parentPath.toLowerCase();
            }
            String className = ParseUtils.getClassName(apiInfo.getInitialAaiPath(), apiInfo.getMethod(), apiInfo.getApiSuffix());

            //生成模版属性
            FtlParam ftlParam = initFtlParam(apiInfo, className, packageName + "." + parentPath);

            //生成自动生成属性
            Structure structure = new Structure()
                    .setFtlParam(ftlParam)
                    .setClassName(className)
                    .setParentPath(parentPath)
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
        private ShowDocParse<ApiInfo> parse;

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        protected IGenerator buildMarker() {
            return new ShowDocGenerator(this);
        }
    }
}
