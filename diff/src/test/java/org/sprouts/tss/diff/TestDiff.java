package org.sprouts.tss.diff;

import org.sprouts.tss.diff.core.PakCodeDiff;
import org.sprouts.tss.diff.dto.CodeDiffResult;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static cn.hutool.core.thread.ThreadUtil.sleep;

@Slf4j
public class TestDiff {

    @Test
    public void test() {
        PakCodeDiff codeDiff = (PakCodeDiff) new PakCodeDiff.Builder()
                .excludes(Set.of("metadata.json", "md", "gitignore"))
                .workDir(System.getProperty("user.home") + "/code_diff/")
                .original("https://minio-api.codewave-test.163yun.com/lowcode-static//source-code-deploy/source-code/csforkf/lq0315/dev/source.tgz")
                .revised("https://minio-api.codewave-test.163yun.com/lowcode-static//source_code_shadow/source-code/csforkf/lq0315/dev/source.tgz")
                .build();
        codeDiff.diff();
        List<CodeDiffResult> codeDiffResults = codeDiff.getResult();
        codeDiffResults.forEach(e -> {
            System.err.println(e.getReport());
            System.err.println("无法解析的文件: " + e.getUnidentifiedList());
        });
        sleep(100000L);
    }
}
