package org.sprouts.tss.diff.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Getter
public abstract class AbstractDiff implements IDiff {
    private final String workDir;
    private final String original;
    private final String revised;
    private final String uuid;

    public AbstractDiff(AbstractDiffBuilder builder) {
        this.original = builder.original;
        this.revised = builder.revised;
        // 处理一下workDir
        this.workDir = builder.workDir.endsWith("/") ? builder.workDir : builder.workDir + "/";
        // 处理一下uuid
        this.uuid = StringUtils.isEmpty(builder.uuid) ? UUID.randomUUID().toString() : builder.uuid;
    }

    @Override
    public abstract void diff();

    @Override
    public abstract void clear();

    /**
     * 删除文件或目录
     *
     * @param workDir 工作目录
     */
    protected void clear(String workDir) {
        File file = new File(workDir);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                Arrays.stream(files).forEach(this::deleteFileOrDirectory);
            }
            if (!file.delete()) {
                log.warn("无法删除工作目录: {}", file.getAbsolutePath());
            }
        }
    }

    /**
     * 删除文件或目录
     *
     * @param file 文件或目录
     */
    protected void deleteFileOrDirectory(File file) {
        if (file.isDirectory()) {
            // 如果是目录，递归删除子文件和子目录
            File[] contents = file.listFiles();
            if (contents != null) {
                Arrays.stream(contents).forEach(this::deleteFileOrDirectory);
            }
        }
        // 删除当前文件或目录
        if (!file.delete()) {
            log.warn("无法删除文件或目录: {}", file.getAbsolutePath());
        }
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public abstract static class AbstractDiffBuilder {
        private String workDir;
        private String original;
        private String revised;
        private String uuid;

        public IDiff build() {
            Optional.ofNullable(workDir).filter(StringUtils::isNotEmpty).orElseThrow(() -> new RuntimeException("工作目录不能为空"));
            Optional.ofNullable(original).filter(StringUtils::isNotEmpty).orElseThrow(() -> new RuntimeException("原始文件不能为空"));
            Optional.ofNullable(revised).filter(StringUtils::isNotEmpty).orElseThrow(() -> new RuntimeException("对比文件不能为空"));
            return buildDiff();
        }

        protected abstract IDiff buildDiff();
    }
}
