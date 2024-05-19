package com.chaohu.generate.core;

import com.chaohu.generate.enums.FileFormatEnum;
import com.chaohu.generate.pojo.ApiInfo;
import com.chaohu.generate.pojo.FtlParam;
import com.chaohu.generate.pojo.Structure;
import com.chaohu.generate.util.ParseUtil;
import com.chaohu.generate.util.StringUtil;
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
    private final String suffix;
    private final int parentPathIndex;

    public ShowDocGenerator(Builder builder) {
        super(builder);
        this.suffix = builder.suffix;
        this.parentPathIndex = builder.parentIndex;
    }

    @Override
    public IGenerator load() {
        List<ApiInfo> list = getParse().execute();
        for (int i = 0; i < list.size(); i++) {
            ApiInfo apiInfo = list.get(i);
            String outputPath = ParseUtil.getOutputPath(getOutputPath());
            String packageName = ParseUtil.getPackageName(getOutputPath());
            String parentPath = null;
            String initialAaiPath = apiInfo.getInitialAaiPath();
            if (initialAaiPath.contains("/")) {
                String[] split = initialAaiPath.split("/");
                parentPath = split.length <= parentPathIndex ? split[1] : split[parentPathIndex];
                parentPath = parentPath.contains("-") || parentPath.contains("_") ? StringUtil.lineToHump(parentPath, false) : parentPath;
                parentPath = parentPath.toLowerCase();
            }
            String className = ParseUtil.getClassName(apiInfo.getInitialAaiPath(), apiInfo.getMethod(), suffix);

            //生成模版属性
            FtlParam ftlParam = initFtlParam(apiInfo, className, packageName + "." + parentPath);

            //生成自动生成属性
            Structure structure = new Structure()
                    .setFtlParam(ftlParam)
                    .setClassName(className)
                    .setParentPath(parentPath)
                    .setOutputPath(outputPath)
                    .setFileSuffix(FileFormatEnum.JAVA.getSuffix());

            if (ftlParam.getApiPath() != null && !ftlParam.getAttrs().isEmpty()) {
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
                .setAttrs(apiInfo.getParam())
                .setClassName(className)
                .setPackagePath(packageName)
                .setDate(new Date().toString());
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<Builder, ApiInfo> {
        private String suffix = "Api";
        private int parentIndex = 1;

        @Override
        protected IGenerator buildMarker() {
            return new ShowDocGenerator(this);
        }
    }
}
