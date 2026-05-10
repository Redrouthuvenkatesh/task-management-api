package com.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taskmanager.enums.TaskStatus;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

/**
 * Query parameter holder for the GET /tasks filtered + paginated endpoint.
 * <p>
 * All fields are optional — omitting a field disables that filter criterion.
 * Pagination defaults: page=0, size=10, sort by createdAt descending.
 */
@Value
@Builder
@Jacksonized
public class TaskFilterRequest {

    /**
     * Filter by exact status (PENDING, IN_PROGRESS, COMPLETED).
     * Omit to return tasks of any status.
     */
    TaskStatus status;

    /**
     * Case-insensitive partial title match (SQL LIKE %title%).
     * Omit to skip title filtering.
     */
    String title;

    /**
     * Include only tasks whose dueDate >= dueDateFrom.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dueDateFrom;

    /**
     * Include only tasks whose dueDate <= dueDateTo.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dueDateTo;

    /**
     * Zero-based page index. Default 0.
     */
    @Builder.Default
    Integer page = 0;

    /**
     * Number of items per page (1–100). Default 10.
     */
    @Builder.Default
    Integer size = 10;

    /**
     * Sort field name (e.g. "createdAt", "title", "dueDate", "status"). Default "createdAt".
     */
    @Builder.Default
    String sortBy = "createdAt";

    /**
     * Sort direction: "asc" or "desc". Default "desc".
     */
    @Builder.Default
    String sortDir = "desc";
}
