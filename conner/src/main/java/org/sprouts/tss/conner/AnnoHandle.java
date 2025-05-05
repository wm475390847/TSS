package org.sprouts.tss.conner;

import org.sprouts.tss.conner.annotation.Collector;
import org.sprouts.tss.conner.annotation.Container;
import org.sprouts.tss.conner.config.IConfigContainer;
import org.sprouts.tss.conner.exception.ListenerException;
import org.sprouts.tss.conner.utils.Property;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 注解解析类
 */
@Slf4j
public class AnnoHandle {

    /**
     * 解析注解
     *
     * @return this
     */
    public <T> void parse(Class<T> tClass) {
        Anno.AnnoBuilder builder = Anno.builder();
        boolean collectAnnotation = tClass.isAnnotationPresent(Collector.class);
        if (collectAnnotation) {
            Collector annotation = tClass.getAnnotation(Collector.class);
            builder.caseCollector(annotation);
        }
        boolean containerAnnotation = tClass.isAnnotationPresent(Container.class);
        if (containerAnnotation) {
            Container annotation = tClass.getAnnotation(Container.class);
            try {
                IConfigContainer container = annotation.value().newInstance();
                container.setProperties(Property.parse());
                container.init();
                builder.configContainer(container);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ListenerException(e.getMessage());
            }
        }
        Context.setAnno(tClass, builder.build());
    }

    @Getter
    @Builder
    public static class Anno {

        Collector caseCollector;

        Container caseContainer;

        IConfigContainer configContainer;

    }

}
