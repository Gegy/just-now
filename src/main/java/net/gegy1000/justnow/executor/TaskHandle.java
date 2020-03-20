package net.gegy1000.justnow.executor;

import net.gegy1000.justnow.Waker;
import net.gegy1000.justnow.future.Future;

import javax.annotation.Nullable;

public final class TaskHandle<T> implements Future<T> {
    final Task<T> task;

    private T result;
    private Waker waker;

    TaskHandle(Task<T> task) {
        this.task = task;
    }

    @Nullable
    @Override
    public T poll(Waker waker) {
        this.waker = waker;
        return this.result;
    }

    void complete(T result) {
        this.result = result;
        if (this.waker != null) {
            this.waker.wake();
        }
    }

    Future<T> steal() {
        this.task.invalidated = true;
        if (this.result != null) {
            return Future.ready(this.result);
        }
        return this.task.future;
    }
}
