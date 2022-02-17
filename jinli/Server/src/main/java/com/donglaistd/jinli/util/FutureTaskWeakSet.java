package com.donglaistd.jinli.util;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;

public class FutureTaskWeakSet {
    private final Set<WeakReference<? extends ScheduledFuture<?>>> futureSet = Collections.newSetFromMap(new WeakHashMap<>());

    public synchronized boolean add(WeakReference<? extends ScheduledFuture<?>> f) {
        return futureSet.add(f);
    }

    public synchronized boolean add(ScheduledFuture<?> f) {
        return add(new WeakReference<>(f));
    }

    public synchronized void clear() {
        futureSet.forEach(f -> Objects.requireNonNull(f.get()).cancel(true));
    }

    public Set<WeakReference<? extends ScheduledFuture<?>>> getFutureSet() {
        return futureSet;
    }
}
