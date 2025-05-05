package org.sprouts.tss.diff.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Set;

@Slf4j
@Getter
public abstract class AbstractCodeDiff<T> extends AbstractDiff implements ICodeDiff<T> {

    /**
     * 排除的文件
     */
    private final Set<String> excludes;

    /**
     * 子工作目录
     */
    private String subWorkDir;

    @Getter
    @Setter
    private T result;

    public AbstractCodeDiff(AbstractCodeDiffBuilder builder) {
        super(builder);
        this.excludes = builder.excludes;
    }

    public void diff() {
        subWorkDir = getWorkDir() + getUuid() + "/";
        File file = new File(subWorkDir);
        if (!file.exists()) {
            log.debug("创建文件夹{}: {}", subWorkDir, file.mkdirs());
        }
        diffCode();
    }

    public abstract void diffCode();

    public void clear() {
        clear(subWorkDir);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public abstract static class AbstractCodeDiffBuilder extends AbstractDiffBuilder {

        private Set<String> excludes;

        protected IDiff buildDiff() {
            return buildCodeDiff();
        }

        protected abstract IDiff buildCodeDiff();
    }
}
