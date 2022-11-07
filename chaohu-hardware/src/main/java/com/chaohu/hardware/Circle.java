package com.chaohu.hardware;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangmin
 */
@Slf4j
public class Circle {

    public static void newCircle(Runnable runnable, String name, long interval) {
        ThreadUtil.newThread(() -> {
            while (true) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(String.valueOf(e));
                } finally {
                    ThreadUtil.safeSleep(interval);
                }
            }
        }, "circle-" + name + "-" + RandomUtil.randomNumbers(8), true).start();
    }

}