package net.gegy1000.justnow.executor;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

public final class TaskQueue {
    private final LinkedBlockingDeque<Task<?>> tasks = new LinkedBlockingDeque<>();

    public void clear() {
        this.tasks.clear();
    }

    public void enqueue(Task<?> task) {
        if (task.isInvalid()) return;
        this.tasks.add(task);
    }

    public boolean remove(Task<?> task) {
        return this.tasks.remove(task);
    }

    public Task<?> take() throws InterruptedException {
        return this.tasks.take();
    }

    public Iterable<Task<?>> drain() {
        return () -> new Iterator<Task<?>>() {
            @Override
            public boolean hasNext() {
                return !TaskQueue.this.tasks.isEmpty();
            }

            @Override
            public Task<?> next() {
                return TaskQueue.this.tasks.remove();
            }
        };
    }

    public Waker waker(Task task) {
        return new Waker(task);
    }

    public class Waker implements net.gegy1000.justnow.Waker {
        private final Task<?> task;
        boolean awoken;

        private Waker(Task<?> task) {
            this.task = task;
        }

        synchronized void reset() {
            this.awoken = false;
        }

        @Override
        public synchronized void wake() {
            if (!this.awoken) {
                this.awoken = true;
                TaskQueue.this.enqueue(this.task);
            }
        }
    }
}
