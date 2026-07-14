package com.ailegacy.modernization.copilot.infrastructure.config.diagnostics;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared, plain-static state for the startup-hang diagnostics. Deliberately
 * NOT a Spring bean: {@link StartupWatchdog} is a plain {@link Thread}
 * started in {@code main()} before any {@code ApplicationContext} exists, so
 * it cannot receive anything via dependency injection. {@link
 * BeanCreationTimingLogger} (a {@code BeanPostProcessor}, itself created very
 * early in the container lifecycle) writes to this; the watchdog thread reads
 * from it, independently of whatever phase Spring startup is stuck in.
 */
final class StartupProgressTracker {

    private static final AtomicInteger completedCount = new AtomicInteger(0);
    private static volatile String lastCompletedBean = "(none yet)";
    private static final Map<String, Instant> inProgress = new ConcurrentHashMap<>();

    private StartupProgressTracker() {
    }

    static void recordStarted(String beanName) {
        inProgress.put(beanName, Instant.now());
    }

    static void recordCompleted(String beanName) {
        inProgress.remove(beanName);
        completedCount.incrementAndGet();
        lastCompletedBean = beanName;
    }

    static int getCompletedCount() {
        return completedCount.get();
    }

    static String getLastCompletedBean() {
        return lastCompletedBean;
    }

    /**
     * Beans whose "before" hook has fired but whose "after" hook has not -
     * i.e. beans that are, right now, still being constructed or initialized.
     * In a genuine hang, this map will contain exactly the one bean that
     * never returns.
     */
    static Map<String, Instant> getInProgress() {
        return Map.copyOf(inProgress);
    }

}
