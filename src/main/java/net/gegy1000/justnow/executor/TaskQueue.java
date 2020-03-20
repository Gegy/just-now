package net.gegy1000.justnow.executor;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

public final class TaskQueue {
    private final LinkedBlockingDeque<Task<?>> tasks = new LinkedBlockingDeque<>();

    public void clear() {
        this.tasks.clear();
    }

    public void enqueue(Task<?> task) {
        if (task.invalidated) return;
        this.tasks.add(task);
    }

    public boolean remove(Task<?> task) {
        return this.tasks.remove(task);
    }

    public Task<?> take() throws InterruptedException {
        return this.tasks.take();
    }

    public Stream<Task<?>> drain() {
        Stream.Builder<Task<?>> builder = Stream.builder();
        while (!this.tasks.isEmpty()) {
            builder.accept(this.tasks.remove());
        }
        return builder.build();
    }
}
