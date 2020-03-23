package net.gegy1000.justnow.executor;

import java.util.Collection;
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

    public void drainTo(Collection<Task<?>> target) {
        while (!this.tasks.isEmpty()) {
            target.add(this.tasks.remove());
        }
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
