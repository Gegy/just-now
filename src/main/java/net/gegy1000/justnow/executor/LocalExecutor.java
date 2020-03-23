package net.gegy1000.justnow.executor;

import net.gegy1000.justnow.future.Future;

import java.util.ArrayList;
import java.util.Collection;

public final class LocalExecutor {
    private final TaskQueue taskQueue = new TaskQueue();
    private final Collection<Task<?>> drainBuffer = new ArrayList<>();

    public <T> TaskHandle<T> spawn(Future<T> future) {
        Task<T> task = new Task<>(future, this.taskQueue);
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
            task.advance();
        }
    }

    public void advanceAll() {
        this.drainBuffer.clear();
        this.taskQueue.drainTo(this.drainBuffer);

        for (Task<?> task : this.drainBuffer) {
            task.advance();
        }
    }
}
