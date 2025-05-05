package org.sprouts.tss.diff.core;

import org.sprouts.tss.diff.dto.CodeDiffResult;
import org.sprouts.tss.diff.exception.DiffException;
import org.sprouts.tss.diff.util.DiffCoreUtils;
import org.sprouts.tss.diff.util.FileUtils;
import org.sprouts.tss.diff.util.StringUtils;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 压缩包文件diff
 * <p>支持zip、tgz格式
 *
 * @author wangmin
 */
@Slf4j
public class PakCodeDiff extends AbstractCodeDiff<List<CodeDiffResult>> {

    private static final String ORIGINAL = "original";
    private static final String REVISED = "revised";
    private final List<String> subFolders = new ArrayList<>();

    /**
     * 创建线程池
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public PakCodeDiff(Builder builder) {
        super(builder);
        this.subFolders.addAll(builder.folders);
    }

    @Override
    public void diffCode() {
        // 工作目录
        String workFolder = getSubWorkDir();
        FileUtils.FileInfo originalFile = FileUtils.parsePath(getOriginal());
        FileUtils.FileInfo revisedFile = FileUtils.parsePath(getRevised());
        CompletableFuture<String> originalFuture = CompletableFuture.supplyAsync(
                () -> downloadAndUncompress(getOriginal(), originalFile.getExtension(), workFolder + ORIGINAL),
                executorService);
        CompletableFuture<String> revisedFuture = CompletableFuture.supplyAsync(
                () -> downloadAndUncompress(getRevised(), revisedFile.getExtension(), workFolder + REVISED),
                executorService);
        // 等待两个任务完成并获取结果
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(originalFuture, revisedFuture)
                .thenRun(() -> {
                    try {
                        String originalSourcePath = originalFuture.get();
                        String revisedSourcePath = revisedFuture.get();
                        diffCode(workFolder, originalSourcePath, revisedSourcePath);
                    } catch (Exception ex) {
                        log.error("源码下载解压或对比异常: {}", ex.getMessage(), ex);
                        throw new DiffException("源码下载解压或对比异常", ex);
                    }
                });
        // 等待所有异步任务完成
        allFutures.join();
    }

    /**
     * 执行代码diff
     *
     * @param workFolder         工作目录
     * @param originalSourcePath 原始文件路径
     * @param revisedSourcePath  对比文件路径
     */
    private void diffCode(String workFolder, String originalSourcePath, String revisedSourcePath) {
        List<String> folders = subFolders.isEmpty() ? List.of("") : subFolders;
        List<CodeDiffResult> collect = folders.stream().map(subFolder -> {
            String originalFolder = StringUtils.isEmpty(subFolder) ? originalSourcePath : originalSourcePath + subFolder;
            String revisedFolder = StringUtils.isEmpty(subFolder) ? revisedSourcePath : revisedSourcePath + subFolder;
            DiffCoreUtils diffCoreUtils = DiffCoreUtils.builder()
                    .originalFolder(originalFolder)
                    .revisedFolder(revisedFolder)
                    .outputPath(workFolder + System.currentTimeMillis() + "_result.html")
                    .excludes(getExcludes())
                    .build()
                    .diffFiles();
            List<String> diffResultStr = diffCoreUtils.getResultStrList();
            List<String> unidentifiedList = diffCoreUtils.getUnidentifiedList();
            // 生成报告
            String report = diffCoreUtils.generateDiffHtml();
            CodeDiffResult codeDiffResult = new CodeDiffResult();
            codeDiffResult.setReport(report);
            codeDiffResult.setResultStrList(diffResultStr);
            codeDiffResult.setUnidentifiedList(unidentifiedList);
            codeDiffResult.setUuid(getUuid());
            return codeDiffResult;
        }).collect(Collectors.toList());
        setResult(collect);
    }

    /**
     * 下载并解压文件
     *
     * @param sourceFileUrl 源文件地址
     * @param outputPath    解压后的文件路径
     * @return 解压后的文件路径
     */
    private String downloadAndUncompress(String sourceFileUrl, String suffix, String outputPath) {
        String targetFile = outputPath + "." + suffix;
        // 支持本地文件和网络文件，本地文件直接使用即可
        String compressedFilePath = sourceFileUrl.contains("http") ? FileUtils.uploadFile(sourceFileUrl, targetFile) : sourceFileUrl;
        return switch (suffix) {
            case "zip" -> FileUtils.unzipFile(compressedFilePath, outputPath);
            case "tgz" -> FileUtils.unTgzFile(compressedFilePath, outputPath);
            default -> throw new DiffException("不支持的解压缩格式" + suffix);
        };
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends AbstractCodeDiffBuilder {

        /**
         * 需要对比的子文件夹
         */
        private List<String> folders = new ArrayList<>();

        public Builder subFolders(List<String> subFolders) {
            folders.addAll(subFolders);
            return this;
        }

        @Override
        protected IDiff buildCodeDiff() {
            return new PakCodeDiff(this);
        }
    }
}
