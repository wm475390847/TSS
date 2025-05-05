package org.sprouts.tss.generator.core;

import org.sprouts.tss.generator.enums.FileFormatEnum;
import org.sprouts.tss.generator.execption.GeneratorException;
import org.sprouts.tss.generator.pojo.Structure;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 生成器的基类
 *
 * @param <P> 对P类型解析器进行生成
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Slf4j
@Getter
public abstract class BaseGenerator<P> implements IGenerator {
    private final int parentPathIndex;
    private final String templatePath;
    private final String templateName;
    private final String outputPath;
    protected Map<Integer, Structure> structureMap = new HashMap<>(64);

    public BaseGenerator(BaseBuilder<?, P> baseBuilder) {
        this.parentPathIndex = baseBuilder.parentPathIndex;
        this.templateName = baseBuilder.templateName;
        this.templatePath = baseBuilder.templatePath;
        this.outputPath = baseBuilder.outputPath;
    }

    @Override
    public void execute() {
        structureMap.forEach((key, value) ->
                Optional.ofNullable(value).ifPresent(v -> {
                    if (StringUtils.isEmpty(v.getOutputPath()) && StringUtils.isEmpty(v.getClassName())) {
                        return;
                    }
                    String outputPath = v.getOutputPath() + v.getParentPath() + "/";
                    File file = new File(outputPath);
                    if (!file.exists()) {
                        log.info("开始创建文件所在文件夹 :{}", outputPath);
                        Optional.of(file).filter(File::mkdirs).orElseThrow(() -> new GeneratorException("文件夹创建失败 !"));
                        log.info("文件所在文件夹创建成功 !");
                    }
                    try {
                        // step1 创建freeMarker配置实例
                        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
                        // step2 获取模版路径
                        configuration.setDirectoryForTemplateLoading(new File(templatePath));
                        // step3 创建数据模型
                        Template template = configuration.getTemplate(templateName);
                        // step4 加载模版文件
                        Map<String, Object> dataMap = v.initDataMap();
                        // step5 生成数据
                        File docFile = new File(outputPath + v.getClassName()
                                + FileFormatEnum.findBySuffix(v.getFileSuffix()).getSuffix());
                        String info = !docFile.exists() ? writerFile(template, dataMap, docFile) : "文件已存在 !";
                        log.info("----------{}----------", info);
                    } catch (IOException | TemplateException e) {
                        log.error("文件生成失败: {}", e.getMessage());
                    }
                }));
    }

    /**
     * 写文件
     *
     * @param template 模版
     * @param dataMap  数据结构
     * @param docFile  输出文件
     * @return 输出结果
     */
    private String writerFile(Template template, Map<String, Object> dataMap, File docFile) throws TemplateException, IOException {
        //增强try，自动关闭资源，被自动关闭的资源必须实现Closeable或AutoCloseable接口
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(docFile.toPath())))) {
            template.process(dataMap, writer);
        }
        return "恭喜~ 文件创建成功 !";
    }

    @Override
    public Integer count() {
        return structureMap.size();
    }

    /**
     * 加载
     *
     * @return IGenerator
     */
    @Override
    public abstract IGenerator load();

    protected abstract static class BaseBuilder<T extends BaseBuilder<?, ?>, P> {
        private int parentPathIndex = 1;
        private String templatePath;
        private String templateName;
        private String outputPath;

        public T templatePath(String templatePath) {
            this.templatePath = templatePath;
            return self();
        }

        public T templateName(String templateName) {
            this.templateName = templateName;
            return self();
        }

        public T outputPath(String outputPath) {
            this.outputPath = outputPath;
            return self();
        }

        public T parentPathIndex(int parentPathIndex) {
            this.parentPathIndex = parentPathIndex;
            return self();
        }

        protected abstract T self();

        public IGenerator build() {
            Optional.ofNullable(templateName).orElseThrow(() -> new GeneratorException("模版名称不能为空"));
            Optional.ofNullable(templatePath).orElseThrow(() -> new GeneratorException("模版路径不能为空"));
            return buildMarker();
        }

        /**
         * 构建
         *
         * @return IMarker
         */
        protected abstract IGenerator buildMarker();
    }
}
