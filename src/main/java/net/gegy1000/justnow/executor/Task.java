package net.gegy1000.justnow.executor;

import net.gegy1000.justnow.Waker;
import net.gegy1000.justnow.future.Future;

import java.util.function.Function;

final class Task<T> {
    final Future<T> future;
    final Waker waker;

    boolean invalidated;
    TaskHandle<T> handle;

    Task(Future<T> future, Function<Task<T>, Waker> waker) {
        this.future = future;
        this.handle = new TaskHandle<>(this);
        this.waker = waker.apply(this);
    }

    void advance() {
        if (this.invalidated) {
            throw new IllegalStateException("task invalid");
        }

        T result = this.future.poll(this.waker);
        if (result != null) {
            this.invalidated = true;
            this.handle.complete(result);
        }
    }
}
