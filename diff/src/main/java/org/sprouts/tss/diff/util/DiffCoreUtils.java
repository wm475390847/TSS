package org.sprouts.tss.diff.util;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 源码对比工具类
 * <p> 基于diff-java-util对比源码并生成diff2Html报告
 *
 * <p> 支持对比文件夹、单个文件，无法对比zip、png等非代码文件
 *
 * @author wangmin
 * @date 2025/1/22 16:57
 */
@Slf4j
@Builder
public class DiffCoreUtils {

    /**
     * 源文件夹
     */
    private String originalFolder;

    /**
     * 目标文件夹
     */
    private String revisedFolder;

    /**
     * 输出文件夹
     */
    private String outputPath;

    /**
     * 排除文件格式
     */
    private Set<String> excludes;

    /**
     * 源文件
     */
    private String originalFile;

    /**
     * 目标文件
     */
    private String revisedFile;

    /**
     * diff结果
     */
    @Getter
    private List<String> resultStrList;

    /**
     * 未识别的文件
     */
    @Getter
    private List<String> unidentifiedList;

    /**
     * diff文件夹
     *
     * @return this
     */
    public DiffCoreUtils diffFiles() {
        Optional.ofNullable(originalFolder).filter(StringUtils::isNotEmpty).orElseThrow(() -> new RuntimeException("初始文件夹不能为空"));
        Optional.ofNullable(revisedFolder).filter(StringUtils::isNotEmpty).orElseThrow(() -> new RuntimeException("目标文件夹不能为空"));
        try {
            // 获取两个文件夹中所有源码文件的映射（使用相对路径作为键）
            Map<Path, Path> folder1Files = getSourceFiles(originalFolder);
            Map<Path, Path> folder2Files = getSourceFiles(revisedFolder);

            // 合并文件路径映射
            Map<Path, List<Path>> allFiles = new HashMap<>();
            for (Map.Entry<Path, Path> entry : folder1Files.entrySet()) {
                Path relativePath = entry.getKey();
                Path folder1FilePath = entry.getValue();
                // 获取或创建一个新列表
                List<Path> paths = allFiles.computeIfAbsent(relativePath, k -> new ArrayList<>());
                // 添加文件夹1的路径
                paths.add(folder1FilePath);
                // 添加文件夹2的路径，如果不存在则为null
                paths.add(folder2Files.getOrDefault(relativePath, null));
            }

            // 插入文件夹2的文件路径，如果文件夹1中没有对应的文件，则插入null
            for (Map.Entry<Path, Path> entry : folder2Files.entrySet()) {
                Path relativePath = entry.getKey();
                Path folder2FilePath = entry.getValue();
                // 如果文件夹1中没有这个文件，则插入null
                if (!allFiles.containsKey(relativePath)) {
                    List<Path> paths = new ArrayList<>();
                    // 文件夹1的路径为null
                    paths.add(null);
                    // 添加文件夹2的路径
                    paths.add(folder2FilePath);
                    allFiles.put(relativePath, paths);
                }
            }
            // 初始化结果集&未识别文件集
            resultStrList = new ArrayList<>();
            unidentifiedList = new ArrayList<>();
            // 遍历所有文件并生成差异报告
            for (Map.Entry<Path, List<Path>> entry : allFiles.entrySet()) {
                Path filePath = entry.getKey();
                log.debug("开始对比文件: {}", filePath);

                List<Path> paths = entry.getValue();
                // 新增或者删除的文件需要特殊处理，如果是null需要直接判断为差异且显示+/-行数据
                List<String> original = paths.get(0) == null ? null : readLine(paths, 0);
                List<String> revised = paths.get(1) == null ? null : readLine(paths, 1);
                Map<Boolean, List<String>> diffMap = diffFileCore(original, revised, filePath, filePath);
                List<String> diffStrList = diffMap.get(true);

                if (diffStrList != null && !diffStrList.isEmpty()) {
                    resultStrList.addAll(diffStrList);
                    log.debug("文件 {} 存在差异，共 {} 行", filePath, diffStrList.size());
                } else {
                    log.debug("文件 {} 无差异", filePath);
                }
            }
        } catch (IOException e) {
            log.error("diff失败", e.fillInStackTrace());
            throw new RuntimeException("diff失败: " + e.getMessage());
        }
        return this;
    }

    /**
     * diff文件
     *
     * @return this
     */
    public DiffCoreUtils diffFile() {
        Optional.ofNullable(originalFile).filter(StringUtils::isNotEmpty).orElseThrow(() -> new RuntimeException("初始文件不能为空"));
        Optional.ofNullable(revisedFile).filter(StringUtils::isNotEmpty).orElseThrow(() -> new RuntimeException("目标文件不能为空"));
        //原始文件
        List<String> original;
        //对比文件
        List<String> revised;
        File originalFile = new File(this.originalFile);
        File revisedFile = new File(this.revisedFile);
        original = readLine(originalFile.toPath());
        revised = readLine(revisedFile.toPath());
        Map<Boolean, List<String>> booleanListMap = diffFileCore(original, revised, originalFile.toPath(), revisedFile.toPath());
        resultStrList = booleanListMap.get(true);
        return this;
    }

    /**
     * 通过两文件的差异diff生成 html文件，打开此 html文件便可看到文件对比的明细内容
     */
    public String generateDiffHtml() {
        Optional.of(outputPath).filter(StringUtils::isNotBlank).orElseThrow(() -> new RuntimeException("输出路径不能为空"));
        StringBuilder builder = new StringBuilder();
        for (String line : resultStrList) {
            builder.append(escapeStr(line));
            builder.append("\n");
        }
        //如果打开html为空白界面，可能cdn加载githubCss失败 ,githubCss 链接可替换为 https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.7.1/styles/github.min.css
        String githubCss = "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.7.1/styles/github.min.css";
        //如果打开html为空白界面，可能cdn加载diff2htmlCss失败 ,diff2htmlCss 链接可替换为 https://cdn.jsdelivr.net/npm/diff2html/bundles/css/diff2html.min.css
        String diff2htmlCss = "https://cdn.jsdelivr.net/npm/diff2html/bundles/css/diff2html.min.css";
        //如果打开html为空白界面，可能cdn加载diff2htmlJs失败, diff2htmlJs 链接可替换为 https://cdn.jsdelivr.net/npm/diff2html/bundles/js/diff2html-ui.min.js
        String diff2htmlJs = "https://cdn.jsdelivr.net/npm/diff2html/bundles/js/diff2html-ui.min.js";
        //如果githubCss、diff2htmlCss、diff2htmlJs都加载失败可从 https://github.com/1506085843/java-file-diff 项目的resources目录下下载css和js手动引入到html
        String template = "<!DOCTYPE html>\n" +
                "<html lang=\"en-us\">\n" +
                "  <head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <link rel=\"stylesheet\" href=\"" + githubCss + "\" />\n" +
                "     <link rel=\"stylesheet\" type=\"text/css\" href=\"" + diff2htmlCss + "\" />\n" +
                "    <script type=\"text/javascript\" src=\"" + diff2htmlJs + "\"></script>\n" +
                "  </head>\n" +
                "  <script>\n" +
                "    const diffString = `\n" +
                "temp\n" +
                "`;\n" +
                "\n" +
                "\n" +
                "     document.addEventListener('DOMContentLoaded', function () {\n" +
                "      var targetElement = document.getElementById('myDiffElement');\n" +
                "      var configuration = {\n" +
                "        drawFileList: true,\n" +
                "        fileListToggle: true,\n" +
                "        fileListStartVisible: false,\n" +
                "        fileContentToggle: true,\n" +
                "        matching: 'lines',\n" +
                "        outputFormat: 'side-by-side',\n" +
                "        synchronisedScroll: true,\n" +
                "        highlight: true,\n" +
                "        renderNothingWhenEmpty: true,\n" +
                "      };\n" +
                "      var diff2htmlUi = new Diff2HtmlUI(targetElement, diffString, configuration);\n" +
                "      diff2htmlUi.draw();\n" +
                "      diff2htmlUi.highlightCode();\n" +
                "    });\n" +
                "  </script>\n" +
                "  <body>\n" +
                "    <div id=\"myDiffElement\"></div>\n" +
                "  </body>\n" +
                "</html>";
        template = template.replace("temp", builder.toString());
        FileWriter f; //文件读取为字符流
        try {
            f = new FileWriter(outputPath);
            BufferedWriter buf = new BufferedWriter(f); //文件加入缓冲区
            buf.write(template); //向缓冲区写入
            buf.close(); //关闭缓冲区并将信息写入文件
            f.close();
        } catch (IOException e) {
            throw new RuntimeException("写HTML文件错误: " + e.getMessage());
        }
        return outputPath;
    }

    /**
     * 获取需要对比的源文件
     *
     * @param folderPath 目录
     * @return 文件映射
     */
    private Map<Path, Path> getSourceFiles(String folderPath) throws IOException {
        Map<Path, Path> files = new HashMap<>();
        Path folderRoot = Paths.get(folderPath);
        Files.walkFileTree(folderRoot, new SimpleFileVisitor<>() {
            @Override
            public @NotNull FileVisitResult visitFile(Path path, @NotNull BasicFileAttributes attrs) {
                if (isSourceFile(path)) {
                    // 使用相对路径作为键
                    files.put(folderRoot.relativize(path), path);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

    /**
     * 判断是否是源文件
     *
     * @param file 文件
     * @return true 是源文件，false不是源文件
     */
    private boolean isSourceFile(Path file) {
        String fileName = file.getFileName().toString();
        String extension = getExtension(fileName);
        // 没有扩展名直接不是属于文件
        if (StringUtils.isEmpty(extension)) {
            return false;
        }

        if (excludes == null || excludes.isEmpty()) {
            return true;
        }

        if (excludes.contains(fileName)) {
            log.debug("文件 {} 被排除", fileName);
            return false;
        }

        if (excludes.contains(extension)) {
            log.debug("文件 {} 因扩展名 {} 被排除", fileName, extension);
            return false;
        }
        // 排除文件
        return true;
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    private String getExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        // 找到最后一个 '.' 的位置
        int dotIndex = fileName.lastIndexOf('.');

        // 如果没有 '.' 或 '.' 在文件名的开头，表示没有扩展名
        if (dotIndex < 1) {
            return "";
        }
        // 返回扩展名（不包括 '.'）
        return fileName.substring(dotIndex + 1);
    }

    /**
     * 读取文件行
     *
     * @param paths 文件路径列表
     * @param index 文件索引
     * @return 文件行
     */
    private List<String> readLine(List<Path> paths, int index) {
        Optional.of(index).filter(e -> e < paths.size()).orElseThrow(() -> new RuntimeException("文件数量错误"));
        return readLine(paths.get(index));
    }

    /**
     * 读取文件行
     *
     * @param path 文件路径
     * @return 文件行
     */
    private List<String> readLine(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            if (e.getMessage().equals("Is a directory")) {
                log.error("文件读取错误: {}", e.getMessage());
                throw new RuntimeException("文件读取错误: " + e.getMessage());
            } else {
                unidentifiedList.add(path.toString());
                log.debug("文件 {} 无法识别", path.getFileName().toString());
            }
        }
        return null;
    }

    /**
     * 对比两文件的差异，返回是否有差异+原始文件+diff格式
     *
     * @param original     原文件内容
     * @param revised      对比文件内容
     * @param originalFile 原始文件
     * @param revisedFile  对比文件
     */
    private Map<Boolean, List<String>> diffFileCore(List<String> original, List<String> revised, Path originalFile, Path revisedFile) {
        Map<Boolean, List<String>> diffMap = new HashMap<>();
        // 检查是否有一个文件为空
        boolean isOriginalEmpty = original == null || original.isEmpty();
        boolean isRevisedEmpty = revised == null || revised.isEmpty();

        // 两个文件都为空表明无法识别，没有差异
        if (isOriginalEmpty && isRevisedEmpty) {
            diffMap.put(false, generateNoDiffPlaceholder(originalFile.toString(), revisedFile.toString()));
            return diffMap;
        }

        // 原始文件为空，所有内容都是新增
        if (isOriginalEmpty) {
            diffMap.put(true, generateAllAddedDiff(revised, revisedFile.toString()));
            return diffMap;
        }

        // 对比文件为空，所有内容都是删除
        if (isRevisedEmpty) {
            diffMap.put(true, generateAllDeletedDiff(original, originalFile.toString()));
            return diffMap;
        }

        // 两文件都不为空，正常进行差异比较
        Patch<String> patch = DiffUtils.diff(original, revised);
        List<String> diff = UnifiedDiffUtils.generateUnifiedDiff(originalFile.toString(), revisedFile.toString(), original, patch, 0);
        // 判断是否有差异
        boolean hasDiff = !diff.isEmpty();
        if (!hasDiff) {
            // 如果没有差异，则返回无差异提示
            diffMap.put(false, generateNoDiffPlaceholder(originalFile.toString(), revisedFile.toString()));
            return diffMap;
        }
        diff.set(1, diff.get(1));
        // 如果第一行没变化则插入@@ -0,0 +0,0 @@
        diff.add(2, "@@ -0,0 +0,0 @@");
        // 原始文件中每行前加空格
        List<String> original1 = original.stream().map(v -> " " + v).collect(Collectors.toList());
        List<String> diffString = insertOrig(original1, diff);
        diffMap.put(true, diffString);
        return diffMap;
    }

    /**
     * 无差异/无法读取文件对比
     *
     * @param originalFileName 原始文件
     * @param revisedFileName  对比文件
     * @return 差异
     */
    private static List<String> generateNoDiffPlaceholder(String originalFileName, String revisedFileName) {
        List<String> placeholder = new ArrayList<>();
        placeholder.add("--- " + originalFileName);
        placeholder.add("+++ " + revisedFileName);
        placeholder.add("@@ -0,0 +0,0 @@");
        return placeholder;
    }

    /**
     * 新增文件的对比
     *
     * @param addedLines 新增的行
     * @param fileName   文件名
     * @return 差异
     */
    private List<String> generateAllAddedDiff(List<String> addedLines, String fileName) {
        List<String> diff = new ArrayList<>();
        diff.add("--- 空文件");
        diff.add("+++ " + fileName);
        diff.add("@@ -0,0 +1," + addedLines.size() + " @@");
        for (String line : addedLines) {
            diff.add("+" + line);
        }
        return diff;
    }

    /**
     * 删除文件的对比
     *
     * @param deletedLines 删除的行
     * @param fileName     文件名
     * @return 差异
     */
    private List<String> generateAllDeletedDiff(List<String> deletedLines, String fileName) {
        List<String> diff = new ArrayList<>();
        diff.add("--- " + fileName);
        diff.add("+++ 空文件");
        diff.add("@@ -1," + deletedLines.size() + " +0,0 @@");
        for (String line : deletedLines) {
            diff.add("-" + line);
        }
        return diff;
    }

    /**
     * 对字符串中的特殊字符进行转义
     *
     * @param linStr 字符串
     * @return 转义后的字符串
     */
    private String escapeStr(String linStr) {
        //如果含有反斜杠对其转义为\\
        if (linStr.contains("\\")) {
            linStr = linStr.replaceAll("\\\\", "\\\\\\\\");
        }
        //如果含有</script>将其转义为<\/script> ，否则浏览器解析到</script>将报错
        if (linStr.contains("</script>")) {
            linStr = linStr.replaceAll("</script>", "<\\\\/script>");
        }
        //如果含有字符串模板的反引号对其转义为\`
        if (linStr.contains("`")) {
            linStr = linStr.replaceAll("`", "\\\\`");
        }
        //如果含有$符号对其转义为\$
        if (linStr.contains("$")) {
            linStr = linStr.replaceAll("\\$", "\\\\\\$");
        }
        return linStr;
    }

    /**
     * 统一差异格式插入到原始文件
     *
     * @param original    原始文件
     * @param unifiedDiff 差异文件
     * @return 统一差异格式插入到原始文件
     */
    private List<String> insertOrig(List<String> original, List<String> unifiedDiff) {
        List<String> result = new ArrayList<>();
        //unifiedDiff中根据@@分割成不同行，然后加入到diffList中
        List<List<String>> diffList = new ArrayList<>();
        List<String> d = new ArrayList<>();
        for (int i = 0; i < unifiedDiff.size(); i++) {
            String u = unifiedDiff.get(i);
            if (u.startsWith("@@") && !"@@ -0,0 +0,0 @@".equals(u) && !u.contains("@@ -1,")) {
                List<String> twoList = new ArrayList<>(d);
                diffList.add(twoList);
                d.clear();
                d.add(u);
                continue;
            }
            if (i == unifiedDiff.size() - 1) {
                d.add(u);
                List<String> twoList = new ArrayList<>(d);
                diffList.add(twoList);
                d.clear();
                break;
            }
            d.add(u);
        }

        //将diffList和原始文件original插入到result，返回result
        for (int i = 0; i < diffList.size(); i++) {
            List<String> diff = diffList.get(i);
            List<String> nexDiff = i == diffList.size() - 1 ? null : diffList.get(i + 1);
            //含有@@的一行
            String simb = i == 0 ? diff.get(2) : diff.get(0);
            String nexSimb = nexDiff == null ? null : nexDiff.get(0);
            //插入到result
            insert(result, diff);
            //解析含有@@的行，得到原文件从第几行开始改变，改变了多少（即增加和减少的行）
            Map<String, Integer> map = getRowMap(simb);
            if (null != nexSimb) {
                Map<String, Integer> nexMap = getRowMap(nexSimb);
                int start = 0;
                if (map.get("orgRow") != 0) {
                    start = map.get("orgRow") + map.get("orgDel") - 1;
                }
                int end = nexMap.get("orgRow") - 2;
                //插入不变的
                insert(result, getOrigList(original, start, end));
            }

            int start = (map.get("orgRow") + map.get("orgDel") - 1);
            start = start == -1 ? 0 : start;
            if (simb.contains("@@ -1,") && null == nexSimb && map.get("orgDel") != original.size()) {
                insert(result, getOrigList(original, start, original.size() - 1));
            } else if (null == nexSimb && (map.get("orgRow") + map.get("orgDel") - 1) < original.size()) {
                insert(result, getOrigList(original, start, original.size() - 1));
            }
        }
        //如果你想知道两文件有几处不同可以放开下面5行代码注释，会在文件名后显示总的不同点有几处（即预览图中的xxx different），放开注释后有一个小缺点就是如果对比的是java、js等代码文件那代码里的关键字就不会高亮颜色显示,有一点不美观。
        int diffCount = diffList.size() - 1;
        if (!"@@ -0,0 +0,0 @@".equals(unifiedDiff.get(2))) {
            diffCount = Math.max(diffList.size(), 1);
        }
        result.set(1, result.get(1) + " ( " + diffCount + " different )");
        return result;
    }

    /**
     * 将源文件中没变的内容插入result
     *
     * @param result          result
     * @param noChangeContent 源文件中没变的内容
     */
    private void insert(List<String> result, List<String> noChangeContent) {
        result.addAll(noChangeContent);
    }

    /**
     * 解析含有@@的行得到修改的行号删除或新增了几行
     *
     * @param str 源文件
     * @return 源文件中没变的内容
     */
    private Map<String, Integer> getRowMap(String str) {
        Map<String, Integer> map = new HashMap<>();
        // 正则校验字符串是不是 "@@ -3,1 +3,1 @@" 这样的diff格式
        String pattern = "^@@\\s-*\\d+,\\d+\\s+\\+\\d+,\\d+\\s+@@$";
        if (Pattern.matches(pattern, str)) {
            String[] sp = str.split(" ");
            String org = sp[1];
            String[] orgSp = org.split(",");
            //源文件要删除行的行号
            map.put("orgRow", Integer.valueOf(orgSp[0].substring(1)));
            //源文件删除的行数
            map.put("orgDel", Integer.valueOf(orgSp[1]));

            String[] revSp = sp[2].split(",");
            //对比文件要增加行的行号
            map.put("revRow", Integer.valueOf(revSp[0].substring(1)));
            map.put("revAdd", Integer.valueOf(revSp[1]));
        }
        return map;
    }

    /**
     * 从原文件中获取指定的部分行
     */
    private List<String> getOrigList(List<String> original1, int start, int end) {
        List<String> list = new ArrayList<>();
        if (!original1.isEmpty() && start <= end && end < original1.size()) {
            for (; start <= end; start++) {
                list.add(original1.get(start));
            }
        }
        return list;
    }
}
