package com.chaohu.conner;

import com.chaohu.conner.config.DingDingConfig;
import com.chaohu.conner.config.ProductConfig;
import com.chaohu.conner.util.Property;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.util.Arrays;

/**
 * 用例收集器基类
 * <P>这里只定义一下方法的执行方向不指定具体执行内容，子类来实现具体的方法</P>
 *
 * @author wangmin
 * @date 2022/5/27 10:01
 */
@Slf4j
public abstract class AbstractCollector {

    /**
     * 初始化case
     *
     * @param method 测试方法
     * @param env    执行环境
     * @param config 产品相关的配置
     * @return 用例数据
     */
    protected abstract Object initInfo(ITestNGMethod method, String env, ProductConfig config);

    /**
     * 保存数据
     *
     * @param info  用例数据
     * @param error 错误信息
     */
    protected abstract void saveInfo(Object info, String error);

    /**
     * 发送通知
     *
     * @param info      用例数据
     * @param config    钉钉配置
     * @param throwable 错误信息
     */
    protected abstract void sendInform(Object info, DingDingConfig config, Throwable throwable);

    /**
     * 初始化用例数据
     *
     * @param iTestResult 测试结果
     * @return 用例数据
     */
    public Object initInfo(ITestResult iTestResult) {
        ITestNGMethod method = iTestResult.getMethod();
        String caseName = method.getMethodName();
        String env = Property.getInstance().parse().getProperty("env");
        System.out.println();
        log.info("==> env: {}, caseName: {}, 是否为调试模式: {}", env, caseName, Context.debug.equals(Context.isOnDebug));
        ProductConfig container = Context.getConfig(ProductConfig.class);
        return initInfo(method, env, container);
    }

    /**
     * 保存数据
     *
     * @param info        用例数据
     * @param iTestResult 测试结果
     */
    public void saveInfo(Object info, ITestResult iTestResult) {
        Throwable throwable = iTestResult.getThrowable();
        String error;
        if (throwable == null) {
            error = null;
        } else {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stackTraceElement = throwable.getStackTrace();
            Arrays.stream(stackTraceElement).forEach(e -> sb.append(e).append("\n"));
            error = throwable + "\n" + sb;
            error = error.substring(0, error.length() - 1);
        }
        saveInfo(info, error);
    }

    /**
     * 发送消息
     *
     * @param info 消息内容
     */
    public void sendInform(Object info, ITestResult iTestResult, DingDingConfig config) {
        if (config != null) {
            sendInform(info, config, iTestResult.getThrowable());
        }
    }
}
