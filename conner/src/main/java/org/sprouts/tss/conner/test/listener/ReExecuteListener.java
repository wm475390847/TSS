package org.sprouts.tss.conner.test.listener;

import lombok.extern.slf4j.Slf4j;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 重新执行拦截器
 *
 * @author wangmin
 * @date 2022/9/23 09:48
 */
@Slf4j
public class ReExecuteListener implements IMethodInterceptor {

    private static Set<Pattern> patterns = new LinkedHashSet<>();

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        String caseNames = System.getProperty("case_names");
        if (caseNames == null || caseNames.trim().isEmpty()) {
            return methods;
        } else {
            log.info("execute caseName: {}", caseNames);
            List<IMethodInstance> methodInstanceList = includeTest(caseNames, methods);
            if (null != methodInstanceList && !methodInstanceList.isEmpty()) {
                return methodInstanceList;
            } else {
                return new ArrayList<>();
            }
        }
    }

    private List<IMethodInstance> includeTest(String testsToInclude, List<IMethodInstance> methods) {
        List<IMethodInstance> matchList = new LinkedList<>();
        if (patterns.isEmpty()) {
            patterns = new LinkedHashSet<>();
            String[] testPatterns = testsToInclude.split(",");
            for (String testPattern : testPatterns) {
                patterns.add(Pattern.compile(testPattern, Pattern.CASE_INSENSITIVE));
            }
        }
        try {
            for (IMethodInstance item : methods) {
                for (Pattern pattern : patterns) {
                    if (pattern.matcher(item.getMethod().getMethodName()).find()) {
                        matchList.add(item);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        log.info("executeCount: {}", matchList.size());
        if (matchList.isEmpty()) {
            matchList = null;
        }
        return matchList;
    }
}
