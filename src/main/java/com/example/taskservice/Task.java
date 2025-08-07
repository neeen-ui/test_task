package com.example.taskservice;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Task {
    private final UUID id = UUID.randomUUID();
    private String description;
    private Duration duration;
    private TaskStatus status = TaskStatus.IN_PROGRESS;
    private final LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime modifiedDate = createdDate;
}
