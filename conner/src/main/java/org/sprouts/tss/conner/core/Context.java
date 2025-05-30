package org.sprouts.tss.conner.core;

import org.sprouts.tss.conner.test.collector.ICollector;
import org.sprouts.tss.conner.core.config.IConfigContainer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局上下文处理
 */
@Slf4j
public class Context {

    private static final Map<Class<?>, AnnoHandle.Anno> ANNO_MAP = new ConcurrentHashMap<>();
    public static boolean DEBUG;

    public static <T> Map<AnnoHandle.Anno, ? extends ICollector> getCollector(Class<T> tClass) {
        if (tClass == null) {
            return null;
        }
        return Optional.ofNullable(getAnno(tClass))
                .map(anno -> Optional.ofNullable(anno.getCaseCollector())
                        .flatMap(collector -> Optional.ofNullable(collector.value())
                                .filter(value -> !value.equals(ICollector.class))
                                .map(value -> {
                                    try {
                                        return Collections.singletonMap(anno, value.getDeclaredConstructor().newInstance());
                                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                                             InvocationTargetException e) {
                                        log.error("收集器获取错误: {}", e.getMessage());
                                        return null;
                                    }
                                }))
                        .orElse(Collections.emptyMap()))
                .orElse(null);
    }


    public static <T> IConfigContainer getContainer(Class<T> tClass) {
        if (tClass == null) {
            return null;
        }
        return Optional.ofNullable(getAnno(tClass))
                .flatMap(anno -> Optional.ofNullable(anno.getConfigContainer()))
                .orElse(null);
    }

    public static <T> AnnoHandle.Anno getAnno(Class<T> tClass) {
        return ANNO_MAP.get(tClass);
    }

    public static <T> void setAnno(Class<T> tClass, AnnoHandle.Anno anno) {
        ANNO_MAP.put(tClass, anno);
    }
}
