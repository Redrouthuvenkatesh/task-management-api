package com.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taskmanager.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

/**
 * Immutable DTO for task create/update requests.
 * <p>
 * Design decision: @Value + @Builder + @Jacksonized produces a fully immutable
 * record-like object that Jackson can deserialize without requiring a default
 * constructor, while still supporting builder-based construction in tests.
 */
@Value
@Builder
@Jacksonized
public class TaskRequest {

    @NotBlank(message = "Title must not be blank")
    String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description;

    @FutureOrPresent(message = "Due date must not be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dueDate;

    /**
     * Optional status override on update. Ignored during create (always PENDING).
     */
    TaskStatus status;
}
