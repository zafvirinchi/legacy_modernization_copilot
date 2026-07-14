package com.ailegacy.modernization.copilot.infrastructure.config.diagnostics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.time.Instant;

/**
 * Registered via {@code SpringApplication.addListeners(...)} in {@code main()}
 * (not as a {@code @Component}), because {@link ApplicationStartingEvent},
 * {@link ApplicationEnvironmentPreparedEvent} and {@link
 * ApplicationContextInitializedEvent} all fire <b>before</b> any {@code
 * ApplicationContext} exists - a regular {@code @EventListener} bean cannot
 * receive them, since there is no bean factory yet to have registered it in.
 * A listener added this way still also receives every later,
 * context-published event (including {@link WebServerInitializedEvent}),
 * since {@code SpringApplication} adds it to the context's own listener list
 * once the context is created.
 *
 * Logs every one of the seven lifecycle events explicitly asked for, in
 * firing order, each with a millisecond timestamp - if this class's log
 * lines stop appearing partway through that sequence, whatever comes next in
 * the list is the phase that never completes.
 */
public final class StartupLifecycleEventLogger implements ApplicationListener<ApplicationEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupLifecycleEventLogger.class);

    private final StartupWatchdog watchdog;

    public StartupLifecycleEventLogger(StartupWatchdog watchdog) {
        this.watchdog = watchdog;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartingEvent) {
            log.info("[LIFECYCLE] ApplicationStartingEvent at {} - SpringApplication.run() has just been called", Instant.now());
        } else if (event instanceof ApplicationEnvironmentPreparedEvent) {
            log.info("[LIFECYCLE] ApplicationEnvironmentPreparedEvent at {} - Environment resolved, no ApplicationContext yet", Instant.now());
        } else if (event instanceof ApplicationContextInitializedEvent) {
            log.info("[LIFECYCLE] ApplicationContextInitializedEvent at {} - ApplicationContext created, not yet refreshed", Instant.now());
        } else if (event instanceof ApplicationPreparedEvent) {
            log.info("[LIFECYCLE] ApplicationPreparedEvent at {} - bean definitions loaded, refresh() about to run", Instant.now());
        } else if (event instanceof ApplicationStartedEvent) {
            log.info("[LIFECYCLE] ApplicationStartedEvent at {} - context refresh completed", Instant.now());
        } else if (event instanceof WebServerInitializedEvent webServerInitializedEvent) {
            log.info("[LIFECYCLE] WebServerInitializedEvent at {} - embedded server listening on port {}",
                    Instant.now(), webServerInitializedEvent.getWebServer().getPort());
        } else if (event instanceof ApplicationReadyEvent) {
            log.info("[LIFECYCLE] ApplicationReadyEvent at {} - application fully up and accepting traffic", Instant.now());
            watchdog.markReady();
        } else if (event instanceof ApplicationFailedEvent applicationFailedEvent) {
            log.error("[LIFECYCLE] ApplicationFailedEvent at {} - startup failed with an exception; " +
                            "if this never logs either, startup is hanging rather than throwing",
                    Instant.now(), applicationFailedEvent.getException());
            watchdog.markReady();
        }
    }

}
