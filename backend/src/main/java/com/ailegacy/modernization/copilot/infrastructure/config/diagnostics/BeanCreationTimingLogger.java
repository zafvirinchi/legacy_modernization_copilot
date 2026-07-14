package com.ailegacy.modernization.copilot.infrastructure.config.diagnostics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.core.Ordered;

import java.time.Instant;

/**
 * Logs a before/after line, with millisecond-precision timestamps, for
 * <b>every</b> bean instantiated and initialized anywhere in the context -
 * including beans this codebase never declares a {@code @Bean} method for
 * (e.g. Spring Boot's autoconfigured {@code MongoClient}, Tomcat's own
 * {@code TomcatServletWebServerFactory}, and so on). This is the single most
 * powerful diagnostic in this package: whichever bean's BEFORE line appears
 * with no matching AFTER line is, definitionally, the one that never returns.
 *
 * Registered with {@link Ordered#HIGHEST_PRECEDENCE} so it wraps every other
 * {@code BeanPostProcessor}'s work too.
 */
final class BeanCreationTimingLogger implements InstantiationAwareBeanPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(BeanCreationTimingLogger.class);

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        StartupProgressTracker.recordStarted(beanName);
        log.info(">>>>>>>> BEFORE bean: {} ({}) at {}", beanName, beanClass.getName(), Instant.now());
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        log.info("<<<<<<<< AFTER bean construction: {} ({}) at {}", beanName, bean.getClass().getName(), Instant.now());
        return true;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        log.info(">>>>>>>> BEFORE bean init: {} ({}) at {}", beanName, bean.getClass().getName(), Instant.now());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.info("<<<<<<<< AFTER bean: {} ({}) at {}", beanName, bean.getClass().getName(), Instant.now());
        StartupProgressTracker.recordCompleted(beanName);
        return bean;
    }

}
