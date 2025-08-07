package com.example.taskservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable UUID id) {
        return service.getTask(id);
    }

    @PostMapping
    public Task createTask(@RequestBody TaskRequest request) {
        return service.createTask(request.description(), Duration.ofSeconds(request.durationSeconds()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelTask(@PathVariable UUID id) {
        try {
            service.cancelTask(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record TaskRequest(String description, long durationSeconds) {}
}
