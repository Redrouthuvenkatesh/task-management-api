package com.taskmanager.exception;

/**
 * Thrown when a requested Task does not exist in the repository.
 * Maps to HTTP 404 NOT FOUND via GlobalExceptionHandler.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task not found with id: " + id);
    }
}
