package com.lniculae.animation_rendered_spring.executors;

import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskStatusManager {
    private ConcurrentMap<String, TaskStatus> taskStatuses;

    public TaskStatusManager() {
        this.taskStatuses = new ConcurrentHashMap<>();
    }

    public void updateStatus(String taskId, TaskStatus status) {
        taskStatuses.put(taskId, status);
    }

    public TaskStatus getStatus(String taskId) {
        return taskStatuses.getOrDefault(taskId, TaskStatus.UnknownTaskStatus);
    }
}
