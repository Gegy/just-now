package net.gegy1000.justnow.executor;

import net.gegy1000.justnow.Waker;
import net.gegy1000.justnow.future.Future;

public final class LocalExecutor {
    private final TaskQueue taskQueue = new TaskQueue();

    public <T> TaskHandle<T> spawn(Future<T> future) {
        Task<T> task = new Task<>(future, EnqueuingWaker::new);
        this.taskQueue.enqueue(task);
        return task.handle;
    }

    public <T> Future<T> steal(TaskHandle<T> handle) {
        this.taskQueue.remove(handle.task);
        return handle.steal();
    }

    public boolean remove(TaskHandle<?> handle) {
        return this.taskQueue.remove(handle.task);
    }

    public void run() throws InterruptedException {
        while (true) {
            Task<?> task = this.taskQueue.take();
            ((EnqueuingWaker) task.waker).reset();
            task.advance();
        }
    }

    public void advanceAll() {
        this.taskQueue.drain().forEach(task -> {
            ((EnqueuingWaker) task.waker).reset();
            task.advance();
        });
    }

    private class EnqueuingWaker implements Waker {
        private final Task<?> task;
        boolean awoken;

        private EnqueuingWaker(Task<?> task) {
            this.task = task;
        }

        void reset() {
            this.awoken = false;
        }

        @Override
        public void wake() {
            if (!this.awoken) {
                LocalExecutor.this.taskQueue.enqueue(this.task);
                this.awoken = true;
            }
        }
    }
}
