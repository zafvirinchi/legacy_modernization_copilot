package com.ailegacy.modernization.copilot.infrastructure.config.diagnostics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;

/**
 * Diagnostic daemon thread, started in {@code main()} before any Spring code
 * runs at all, that prints a startup-progress snapshot every 5 seconds until
 * the application becomes ready (see {@link #markReady()}, called by {@link
 * StartupLifecycleEventLogger} on {@code ApplicationReadyEvent}). Because it
 * is a plain {@link Thread} rather than a Spring bean, it keeps reporting
 * regardless of which phase of {@code ApplicationContext} refresh - or
 * whether Spring has even gotten that far yet - is stuck.
 *
 * After 30 seconds of uptime without becoming ready, escalates to a full
 * dump of every JVM thread's stack, not just blocked/waiting ones.
 */
public final class StartupWatchdog extends Thread {

    private static final Logger log = LoggerFactory.getLogger(StartupWatchdog.class);
    private static final long INTERVAL_MS = 5_000;
    private static final long ESCALATION_THRESHOLD_SECONDS = 30;

    private final long startNanos = System.nanoTime();
    private volatile boolean applicationReady = false;

    public StartupWatchdog() {
        super("startup-watchdog");
        setDaemon(true);
    }

    public void markReady() {
        this.applicationReady = true;
    }

    @Override
    public void run() {
        log.info("[WATCHDOG] Started at {} - will report every {}s until ApplicationReadyEvent fires",
                Instant.now(), INTERVAL_MS / 1000);
        while (!applicationReady) {
            try {
                Thread.sleep(INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (applicationReady) {
                break;
            }
            long uptimeSeconds = (System.nanoTime() - startNanos) / 1_000_000_000L;
            dumpSnapshot(uptimeSeconds);
        }
        log.info("[WATCHDOG] ApplicationReadyEvent observed - watchdog stopping.");
    }

    private void dumpSnapshot(long uptimeSeconds) {
        log.warn("===== STARTUP WATCHDOG =====");
        log.warn("[WATCHDOG] Timestamp             : {}", Instant.now());
        log.warn("[WATCHDOG] Application uptime     : {}s", uptimeSeconds);
        log.warn("[WATCHDOG] Beans completed so far : {}", StartupProgressTracker.getCompletedCount());
        log.warn("[WATCHDOG] Last bean completed    : {}", StartupProgressTracker.getLastCompletedBean());

        Map<String, Instant> inProgress = StartupProgressTracker.getInProgress();
        if (inProgress.isEmpty()) {
            log.warn("[WATCHDOG] Beans currently in progress: none (hang, if any, is outside bean creation - "
                    + "check BeanFactoryPostProcessor/SmartLifecycle/ServletContextInitializer/Tomcat logs below)");
        } else {
            inProgress.forEach((beanName, startedAt) -> log.warn(
                    "[WATCHDOG] Bean currently in progress: '{}' - started at {}, {}ms ago",
                    beanName, startedAt, java.time.Duration.between(startedAt, Instant.now()).toMillis()));
        }

        dumpThreadsInState(Thread.State.BLOCKED, Thread.State.WAITING, Thread.State.TIMED_WAITING);

        if (uptimeSeconds >= ESCALATION_THRESHOLD_SECONDS) {
            log.warn("[WATCHDOG] Uptime >= {}s with no ApplicationReadyEvent - escalating to a full thread dump",
                    ESCALATION_THRESHOLD_SECONDS);
            dumpAllThreads();
        }
        log.warn("===========================");
    }

    private void dumpThreadsInState(Thread.State... states) {
        java.util.Set<Thread.State> wanted = java.util.Set.of(states);
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            Thread thread = entry.getKey();
            if (!wanted.contains(thread.getState())) {
                continue;
            }
            log.warn("[WATCHDOG] Thread '{}' is {} :", thread.getName(), thread.getState());
            for (StackTraceElement element : entry.getValue()) {
                log.warn("    at {}", element);
            }
        }
    }

    private void dumpAllThreads() {
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            Thread thread = entry.getKey();
            log.warn("[WATCHDOG] Thread '{}' (state={}, daemon={}):", thread.getName(), thread.getState(), thread.isDaemon());
            for (StackTraceElement element : entry.getValue()) {
                log.warn("    at {}", element);
            }
        }
    }

}
