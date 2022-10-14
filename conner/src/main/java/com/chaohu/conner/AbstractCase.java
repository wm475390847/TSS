package com.chaohu.conner;

import com.chaohu.conner.exception.ConnerException;
import com.chaohu.conner.listener.ExecuteListener;
import com.chaohu.conner.util.Property;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Listeners;

import java.util.Optional;
import java.util.Properties;

/**
 * 基础case类
 *
 * @author wangmin
 * @date 2021/12/28 7:15 下午
 */
@Slf4j
@Listeners(value = ExecuteListener.class)
public abstract class AbstractCase {

    private static final String DEBUG = System.getProperty("DEBUG", "true");
    private static String IS_ON_DEBUG = "true";
    protected static Properties properties = Property.getInstance().parse();

    public AbstractCase(boolean isCheck) {
        if (isCheck) {
            checkEnv("test");
        }
    }

    /**
     * 校验环境
     *
     * @param env 环境
     */
    public AbstractCase(boolean isCheck, String env) {
        if (isCheck) {
            checkEnv(env);
        }
    }

    /**
     * 校验环境和debug模式
     *
     * @param env       环境
     * @param isOnDebug 是否为debug模式
     */
    public AbstractCase(boolean isCheck, String env, boolean isOnDebug) {
        IS_ON_DEBUG = String.valueOf(isOnDebug);
        if (isCheck) {
            checkEnv(env);
        }
    }

    /**
     * 控制锁
     * <P>日常debug时测试和线上使用一套测试代码，可能会造成误操作情况，所以在此处增加debug+env的判断，
     * 当模式debug=true&env!=test时报错，
     * ci回归时指定-DDebug="false" -P=prod||-P=test都不会触发锁
     */
    private void checkEnv(String env) {
        Context.debug = DEBUG;
        Context.isOnDebug = IS_ON_DEBUG;
        String propertiesEnv = properties.getProperty("env");
        Optional.ofNullable(propertiesEnv).orElseThrow(() -> new ConnerException("请在配置文件中配置变量：env=?"));
        if (DEBUG.equals(IS_ON_DEBUG) && !env.equals(propertiesEnv)) {
            throw new ConnerException("debug模式默认为test环境，如需切换请先修改case父类构造super中env参数");
        }
    }
}
