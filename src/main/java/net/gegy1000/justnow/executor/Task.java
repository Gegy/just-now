package net.gegy1000.justnow.executor;

import net.gegy1000.justnow.future.Future;

final class Task<T> {
    final Future<T> future;
    final TaskQueue.Waker waker;

    private boolean invalidated;
    TaskHandle<T> handle;

    Task(Future<T> future, TaskQueue taskQueue) {
        this.future = future;
        this.handle = new TaskHandle<>(this);
        this.waker = taskQueue.waker(this);
    }

    void invalidate() {
        this.invalidated = true;
    }

    boolean isInvalid() {
        return this.invalidated;
    }

    void advance() {
        if (this.invalidated) {
            throw new IllegalStateException("task invalid");
        }

        try {
            T result = this.future.poll(this.waker);
            if (result != null) {
                this.invalidated = true;
                this.handle.completeOk(result);
            }
        } catch (Throwable exception) {
            this.invalidated = true;
            this.handle.completeErr(exception);
        }
    }
}
