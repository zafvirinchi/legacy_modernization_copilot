package com.ailegacy.modernization.copilot.infrastructure.config.diagnostics;

import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

/**
 * Registers the remaining Northflank startup-hang diagnostic checkpoints not
 * already covered by {@link BeanCreationTimingLogger} (every bean,
 * generically) or {@link StartupLifecycleEventLogger} (the seven
 * SpringApplication/context lifecycle events):
 *
 * <ul>
 *   <li>{@link BeanDefinitionRegistryPostProcessor} - the earliest possible
 *       checkpoint in the whole refresh sequence, plus a best-effort listing
 *       of every {@code BeanFactoryPostProcessor} bean definition visible at
 *       that point. Note: Spring provides no safe, non-invasive way to wrap
 *       arbitrary third-party {@code BeanFactoryPostProcessor}s with
 *       individual before/after timing without proxying them, which risks
 *       altering their behavior - explicitly out of scope per instructions.
 *       This checkpoint instead answers "did we even reach this phase" and
 *       "what BFPPs exist", which combined with the DEBUG-level Spring logs
 *       already enabled gives full visibility into this phase's duration.</li>
 *   <li>{@link SmartInitializingSingleton} - fires once every singleton bean
 *       in the context has been both constructed and initialized, i.e. right
 *       after {@code finishBeanFactoryInitialization()} completes.</li>
 *   <li>Two {@link SmartLifecycle} beans at the minimum and maximum possible
 *       phase values, bracketing every other {@code SmartLifecycle} bean's
 *       {@code start()} call - including Tomcat's own {@code
 *       WebServerStartStopLifecycle}, which is what actually performs the
 *       bind/listen Spring Boot logs as "Tomcat started on port(s)".</li>
 *   <li>A {@link ServletContextInitializer} bean, invoked as part of the
 *       embedded server's self-initialization (this is what triggers {@code
 *       DispatcherServlet} setup).</li>
 *   <li>A Tomcat-level {@code LifecycleListener}, registered via {@link
 *       WebServerFactoryCustomizer}, logging Tomcat's own internal state
 *       transitions (STARTING_PREP, STARTING, STARTED, etc.).</li>
 * </ul>
 *
 * Every {@code @Bean} method here is deliberately {@code static} where the
 * bean type requires very early instantiation (post-processors), per Spring's
 * own recommendation, so it can run before the surrounding {@code
 * @Configuration} class itself is fully processed as a regular bean.
 */
@Slf4j
@Configuration
public class StartupDiagnosticsConfig {

    @Bean
    public static BeanCreationTimingLogger beanCreationTimingLogger() {
        return new BeanCreationTimingLogger();
    }

    @Bean
    public static BeanDefinitionRegistryPostProcessor startupPhaseVisibilityLogger() {
        return new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
                log.info("[LIFECYCLE] BeanDefinitionRegistryPostProcessor phase entered at {}", Instant.now());
                for (String name : registry.getBeanDefinitionNames()) {
                    String className = registry.getBeanDefinition(name).getBeanClassName();
                    if (className != null && (className.contains("BeanFactoryPostProcessor")
                            || className.contains("PostProcessor") && className.toLowerCase().contains("config"))) {
                        log.info("[LIFECYCLE] BeanDefinitionRegistry contains candidate post-processor bean definition: '{}' ({})",
                                name, className);
                    }
                }
            }

            @Override
            public void postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) {
                log.info("[LIFECYCLE] BeanFactoryPostProcessor phase (postProcessBeanFactory) reached at {}", Instant.now());
                for (String name : beanFactory.getBeanNamesForType(org.springframework.beans.factory.config.BeanFactoryPostProcessor.class, false, false)) {
                    log.info("[LIFECYCLE] BeanFactoryPostProcessor bean present in factory: '{}'", name);
                }
            }
        };
    }

    @Bean
    public SmartInitializingSingleton allSingletonsInstantiatedCheckpoint() {
        return () -> log.info("[LIFECYCLE] SmartInitializingSingleton.afterSingletonsInstantiated() at {} - "
                + "every regular singleton bean has now been constructed AND initialized", Instant.now());
    }

    @Bean
    public SmartLifecycle firstSmartLifecycleCheckpoint() {
        return new SmartLifecycle() {
            private volatile boolean running = false;

            @Override
            public void start() {
                log.info("[LIFECYCLE] Earliest-phase SmartLifecycle.start() at {} - "
                        + "about to start every other SmartLifecycle bean (including Tomcat's own)", Instant.now());
                running = true;
            }

            @Override
            public void stop() {
                running = false;
            }

            @Override
            public boolean isRunning() {
                return running;
            }

            @Override
            public int getPhase() {
                return Integer.MIN_VALUE;
            }
        };
    }

    @Bean
    public SmartLifecycle lastSmartLifecycleCheckpoint() {
        return new SmartLifecycle() {
            private volatile boolean running = false;

            @Override
            public void start() {
                log.info("[LIFECYCLE] Latest-phase SmartLifecycle.start() at {} - "
                        + "every other SmartLifecycle bean (including Tomcat's own WebServerStartStopLifecycle) "
                        + "has now started successfully", Instant.now());
                running = true;
            }

            @Override
            public void stop() {
                running = false;
            }

            @Override
            public boolean isRunning() {
                return running;
            }

            @Override
            public int getPhase() {
                return Integer.MAX_VALUE;
            }
        };
    }

    @Bean
    public ServletContextInitializer servletContextInitializerCheckpoint() {
        return (ServletContext servletContext) -> {
            log.info("[LIFECYCLE] ServletContextInitializer.onStartup() BEFORE at {} (this is what triggers "
                    + "DispatcherServlet setup)", Instant.now());
            // Nothing to do - this bean exists purely to bracket servlet context
            // initialization with a timestamp; Spring invokes each registered
            // ServletContextInitializer in turn.
            log.info("[LIFECYCLE] ServletContextInitializer.onStartup() AFTER at {}", Instant.now());
        };
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatLifecycleListenerLogger() {
        return factory -> factory.addContextLifecycleListeners((LifecycleEvent event) ->
                log.info("[LIFECYCLE] Tomcat Context lifecycle event '{}' at {}", event.getType(), Instant.now()));
    }

}
