package com.example.taskservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testTaskLifecycle() throws InterruptedException {
        //создание задачи
        Map<String, Object> request = Map.of(
                "description", "Test task",
                "durationSeconds", 2
        );
        ResponseEntity<Task> createResponse = restTemplate.postForEntity("/tasks", request, Task.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Task createdTask = createResponse.getBody();
        assertThat(createdTask).isNotNull();
        UUID taskId = createdTask.getId();

        //получение всех задач
        ResponseEntity<Task[]> allTasks = restTemplate.getForEntity("/tasks", Task[].class);
        assertThat(allTasks.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(List.of(allTasks.getBody())).extracting(Task::getId).contains(taskId);

        //ожидаем статус DONE
        Thread.sleep(3000);

        //проверка смены статуса
        ResponseEntity<Task> getTask = restTemplate.getForEntity("/tasks/" + taskId, Task.class);
        assertThat(getTask.getBody().getStatus()).isEqualTo(TaskStatus.DONE);

        //попытка отменить задачу и получить ошибку
        ResponseEntity<String> cancelResponse = restTemplate.exchange("/tasks/" + taskId, HttpMethod.DELETE, null, String.class);
        assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(cancelResponse.getBody()).contains("Cannot cancel");
    }
}
