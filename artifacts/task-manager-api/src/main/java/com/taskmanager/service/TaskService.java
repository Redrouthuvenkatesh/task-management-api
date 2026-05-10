package com.taskmanager.service;

import com.taskmanager.dto.PagedResponse;
import com.taskmanager.dto.TaskFilterRequest;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;

import java.util.List;

/**
 * Service contract for all Task business operations.
 * <p>
 * Design decision: programming to an interface decouples the controller from the
 * concrete implementation, making it trivial to swap implementations without
 * touching the controller layer.
 */
public interface TaskService {

    /**
     * Retrieve all tasks (no filtering, no pagination).
     * Kept for backward compatibility.
     */
    List<TaskResponse> getAllTasks();

    /**
     * Retrieve tasks with optional filtering and pagination.
     *
     * @param filter criteria: status, title, dueDateFrom, dueDateTo, page, size, sortBy, sortDir
     * @return a {@link PagedResponse} wrapping the matching tasks and page metadata
     */
    PagedResponse<TaskResponse> getFilteredTasks(TaskFilterRequest filter);

    /**
     * Retrieve a single task by its ID.
     *
     * @throws com.taskmanager.exception.TaskNotFoundException if no task with the given ID exists
     */
    TaskResponse getTaskById(Long id);

    /**
     * Create a new task. Status is always initialized to PENDING.
     */
    TaskResponse createTask(TaskRequest request);

    /**
     * Replace all fields of an existing task.
     *
     * @throws com.taskmanager.exception.TaskNotFoundException if no task with the given ID exists
     */
    TaskResponse updateTask(Long id, TaskRequest request);

    /**
     * Delete a task by its ID.
     *
     * @throws com.taskmanager.exception.TaskNotFoundException if no task with the given ID exists
     */
    void deleteTask(Long id);

    /**
     * Transition a task's status to COMPLETED.
     *
     * @throws com.taskmanager.exception.TaskNotFoundException if no task with the given ID exists
     */
    TaskResponse markTaskComplete(Long id);
}
