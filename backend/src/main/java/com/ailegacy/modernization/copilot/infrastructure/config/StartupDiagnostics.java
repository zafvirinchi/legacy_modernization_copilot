package com.ailegacy.modernization.copilot.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporary diagnostic instrumentation for the Northflank investigation: the
 * process reportedly never logs "Tomcat started on port(s)" nor completes
 * {@code ApplicationContext} refresh, so {@code ApplicationReadyEvent} never
 * fires either. This class provides two complementary instruments:
 *
 * <ol>
 *   <li>{@code @EventListener} methods for {@link ApplicationStartedEvent},
 *       {@link ApplicationReadyEvent}, {@link WebServerInitializedEvent} and
 *       {@link ApplicationFailedEvent} - each is functionally identical to a
 *       dedicated {@code ApplicationListener<TheEvent>} bean (Spring registers
 *       an {@code ApplicationListener} adapter for every {@code @EventListener}
 *       method internally), just without needing four separate classes. If
 *       none of the first three ever log, and {@code ApplicationFailedEvent}
 *       doesn't log either, startup is hanging (not throwing) before Spring
 *       even gets far enough to publish any lifecycle event at all.</li>
 *   <li>A generic {@link InstantiationAwareBeanPostProcessor} that logs a
 *       before/after timestamp for <b>every single bean in the context</b>,
 *       including ones this project doesn't declare {@code @Bean} methods
 *       for itself (e.g. Spring Boot's autoconfigured {@code MongoClient}).
 *       Hand-instrumenting only this project's own {@code @Configuration}
 *       classes (see {@link SecurityConfig}, {@link OpenApiConfig},
 *       {@link LangChain4jConfig}, {@link StartupBanner}) cannot see into
 *       autoconfigured beans - this can. Whichever bean's "before" line
 *       appears with no matching "after" line is the one that never returns.</li>
 * </ol>
 *
 * Remove this whole class (and the {@code spring.main.application-startup} /
 * {@code logging.level.org.springframework} diagnostic settings in
 * {@code application.yml}) once the hang is found and fixed - none of this is
 * meant to be permanent.
 */
@Slf4j
@Component
public class StartupDiagnostics {

    private static final String TAG = "[STARTUP-DIAG]";

    @EventListener
    public void onApplicationStarted(ApplicationStartedEvent event) {
        log.info("{} ApplicationStartedEvent fired at {} - context refresh completed, bean definitions loaded",
                TAG, Instant.now());
    }

    @EventListener
    public void onWebServerInitialized(WebServerInitializedEvent event) {
        log.info("{} WebServerInitializedEvent fired at {} - embedded server is listening on port {}",
                TAG, Instant.now(), event.getWebServer().getPort());
    }

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.info("{} ApplicationReadyEvent fired at {} - application is fully up and accepting traffic",
                TAG, Instant.now());
    }

    @EventListener
    public void onApplicationFailed(ApplicationFailedEvent event) {
        log.error("{} ApplicationFailedEvent fired at {} - startup failed with an exception (see below); " +
                        "if this never logs either, startup is hanging rather than throwing",
                TAG, Instant.now(), event.getException());
    }

    /**
     * Logs before/after every bean's instantiation and initialization,
     * project-wide, including autoconfigured beans this codebase never
     * declares a {@code @Bean} method for. Registered with the highest
     * possible precedence so it wraps every other bean's creation.
     */
    @Bean
    public static InstantiationAwareBeanPostProcessor beanCreationTimingLogger() {
        return new BeanCreationTimingLogger();
    }

    private static class BeanCreationTimingLogger implements InstantiationAwareBeanPostProcessor, Ordered {

        private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BeanCreationTimingLogger.class);
        private final Map<String, Long> instantiationStartNanos = new ConcurrentHashMap<>();
        private final Map<String, Long> initializationStartNanos = new ConcurrentHashMap<>();

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
            instantiationStartNanos.put(beanName, System.nanoTime());
            LOG.info("{} >> constructing bean '{}' ({}) at {}", TAG, beanName, beanClass.getName(), Instant.now());
            return null;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) {
            Long start = instantiationStartNanos.remove(beanName);
            if (start != null) {
                long tookMs = (System.nanoTime() - start) / 1_000_000;
                LOG.info("{} << constructed bean '{}' at {} ({} ms)", TAG, beanName, Instant.now(), tookMs);
            }
            return true;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            initializationStartNanos.put(beanName, System.nanoTime());
            LOG.info("{} >> initializing bean '{}' at {}", TAG, beanName, Instant.now());
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            Long start = initializationStartNanos.remove(beanName);
            if (start != null) {
                long tookMs = (System.nanoTime() - start) / 1_000_000;
                LOG.info("{} << initialized bean '{}' at {} ({} ms)", TAG, beanName, Instant.now(), tookMs);
            }
            return bean;
        }

    }

}
