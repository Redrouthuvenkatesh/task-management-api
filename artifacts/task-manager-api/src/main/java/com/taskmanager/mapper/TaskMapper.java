package com.taskmanager.mapper;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.enums.TaskStatus;
import org.springframework.stereotype.Component;

/**
 * Stateless mapper component handling conversions between Task entity and DTOs.
 * <p>
 * Design decision: manual mapping is used here instead of MapStruct to avoid
 * annotation processor conflicts with Lombok. For larger projects with many
 * entities, switching to MapStruct is recommended.
 */
@Component
public class TaskMapper {

    /**
     * Convert a TaskRequest DTO to a new Task entity.
     * Status is always set to PENDING on creation regardless of input.
     */
    public Task toEntity(TaskRequest request) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(TaskStatus.PENDING)
                .build();
    }

    /**
     * Apply a TaskRequest update onto an existing Task entity (partial update pattern).
     * Only non-null fields from the request overwrite entity fields.
     */
    public void updateEntity(Task task, TaskRequest request) {
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
    }

    /**
     * Convert a Task entity to a TaskResponse DTO for API output.
     */
    public TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
