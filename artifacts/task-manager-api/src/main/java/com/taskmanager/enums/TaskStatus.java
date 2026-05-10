package com.taskmanager.enums;

/**
 * Enum representing the lifecycle states of a Task.
 * <p>
 * PENDING     → Task created but not yet started
 * IN_PROGRESS → Task actively being worked on
 * COMPLETED   → Task finished; terminal state set via PATCH /tasks/{id}/complete
 */
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}
