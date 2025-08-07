package com.example.taskservice;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
public class TaskService {

    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTask(UUID id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NoSuchElementException("Task not found");
        }
        return task;
    }

    public Task createTask(String description, Duration duration) {
        Task task = new Task();
        task.setDescription(description);
        task.setDuration(duration);
        tasks.put(task.getId(), task);

        // Запланировать смену статуса
        scheduler.schedule(() -> markTaskAsDone(task.getId()), duration.toMillis(), TimeUnit.MILLISECONDS);

        return task;
    }

    public void cancelTask(UUID id) {
        Task task = getTask(id);
        synchronized (task) {
            if (task.getStatus() == TaskStatus.DONE) {
                throw new IllegalStateException("Cannot cancel a task that is already DONE");
            }
            if (task.getStatus() == TaskStatus.CANCELED) return;
            task.setStatus(TaskStatus.CANCELED);
            task.setModifiedDate(LocalDateTime.now());
        }
    }

    private void markTaskAsDone(UUID id) {
        Task task = tasks.get(id);
        if (task != null && task.getStatus() == TaskStatus.IN_PROGRESS) {
            synchronized (task) {
                task.setStatus(TaskStatus.DONE);
                task.setModifiedDate(LocalDateTime.now());
            }
        }
    }
}
