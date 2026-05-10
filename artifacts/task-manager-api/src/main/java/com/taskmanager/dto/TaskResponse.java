package com.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taskmanager.enums.TaskStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Immutable DTO returned to API consumers for task data.
 * <p>
 * Date formats are standardized to ISO-8601 for broad client compatibility.
 */
@Value
@Builder
public class TaskResponse {

    Long id;
    String title;
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dueDate;

    TaskStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt;
}
