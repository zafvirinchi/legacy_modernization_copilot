package com.ailegacy.modernization.copilot.infrastructure.ai;

import org.springframework.beans.factory.ObjectProvider;

/**
 * Minimal {@link ObjectProvider} test double that always resolves to a fixed
 * (possibly null) value, standing in for the real Spring-managed provider the
 * analyzer classes use to lazily resolve {@code ChatLanguageModel} without
 * forcing its creation during application startup.
 */
class FixedObjectProvider<T> implements ObjectProvider<T> {

    private final T value;

    FixedObjectProvider(T value) {
        this.value = value;
    }

    @Override
    public T getObject() {
        return value;
    }

    @Override
    public T getObject(Object... args) {
        return value;
    }

    @Override
    public T getIfAvailable() {
        return value;
    }

    @Override
    public T getIfUnique() {
        return value;
    }

}
