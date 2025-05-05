package org.sprouts.tss.diff.core;

import org.sprouts.tss.diff.dto.CodeDiffResult;
import org.sprouts.tss.diff.exception.DiffException;
import org.sprouts.tss.diff.util.DiffCoreUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * 文件diff
 * <p>支持文件对比，支持目录对比，非压缩包类型
 *
 * @author wangmin
 */
@Slf4j
public class FileCodeDiff extends AbstractCodeDiff<List<CodeDiffResult>> {
    public FileCodeDiff(Builder builder) {
        super(builder);
    }

    @Override
    public void diffCode() {
        String workFolder = getSubWorkDir();
        try {
            // 执行源码对比
            File originalFile = new File(getOriginal());
            File revisedFile = new File(getRevised());
            DiffCoreUtils diffCoreUtils;
            DiffCoreUtils.DiffCoreUtilsBuilder builder = DiffCoreUtils.builder()
                    .outputPath(workFolder + "result.html")
                    .excludes(getExcludes());
            if (originalFile.isFile() && revisedFile.isFile()) {
                builder.revisedFile(getRevised()).originalFile(getOriginal());
                diffCoreUtils = builder.build().diffFile();
            } else if (originalFile.isDirectory() && revisedFile.isDirectory()) {
                builder.originalFolder(getOriginal()).revisedFolder(getRevised());
                diffCoreUtils = builder.build().diffFiles();
            } else {
                throw new DiffException("原始文件和对比文件须同时为目录或文件");
            }
            List<String> diffResultStr = diffCoreUtils.getResultStrList();
            String report = diffCoreUtils.generateDiffHtml();
            CodeDiffResult codeDiffResult = new CodeDiffResult();
            codeDiffResult.setResultStrList(diffResultStr);
            codeDiffResult.setReport(report);
            codeDiffResult.setUuid(getUuid());
            List<CodeDiffResult> codeDiffResults = List.of(codeDiffResult);
            setResult(codeDiffResults);
        } catch (Exception e) {
            // 捕获 thenRun 中可能抛出的异常
            log.error("源码对比异常: " + e.getMessage(), e);
            throw new DiffException("源码对比异常", e);
        }
    }

    public static class Builder extends AbstractCodeDiffBuilder {

        @Override
        protected IDiff buildCodeDiff() {
            return new FileCodeDiff(this);
        }
    }
}
