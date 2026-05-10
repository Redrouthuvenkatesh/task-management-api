package com.taskmanager.controller;

import com.taskmanager.dto.ApiResponse;
import com.taskmanager.dto.PagedResponse;
import com.taskmanager.dto.TaskFilterRequest;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller exposing the Task Management API.
 * <p>
 * Design decisions:
 * - All responses are wrapped in {@link ResponseEntity} for explicit HTTP status control.
 * - @Valid triggers Spring's validation chain before the method body executes.
 * - GET /tasks accepts optional query parameters for filtering and pagination.
 *   All params are optional; omitting them returns the full first page.
 */
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tasks", description = "Task Management API — CRUD + lifecycle + pagination + filtering")
public class TaskController {

    private final TaskService taskService;

    // -------------------------------------------------------------------------
    // GET /tasks — with optional filtering + pagination
    // -------------------------------------------------------------------------

    @GetMapping
    @Operation(
        summary = "Get tasks (with optional filtering and pagination)",
        description = """
            Retrieve tasks with optional filter criteria and pagination.
            All query parameters are optional. Omitting all returns the first page of all tasks.
            
            **Filter params:**
            - `status` — exact match: `PENDING`, `IN_PROGRESS`, or `COMPLETED`
            - `title` — case-insensitive partial match
            - `dueDateFrom` — include tasks with dueDate on or after this date (yyyy-MM-dd)
            - `dueDateTo` — include tasks with dueDate on or before this date (yyyy-MM-dd)
            
            **Pagination params:**
            - `page` — zero-based page index (default: 0)
            - `size` — items per page, 1–100 (default: 10)
            - `sortBy` — field to sort by: `id`, `title`, `status`, `dueDate`, `createdAt`, `updatedAt` (default: `createdAt`)
            - `sortDir` — sort direction: `asc` or `desc` (default: `desc`)
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "Tasks retrieved successfully (paginated)")
    })
    public ResponseEntity<PagedResponse<TaskResponse>> getAllTasks(

            @Parameter(description = "Filter by task status")
            @RequestParam(required = false) TaskStatus status,

            @Parameter(description = "Case-insensitive partial title match")
            @RequestParam(required = false) String title,

            @Parameter(description = "Due date range start (yyyy-MM-dd, inclusive)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,

            @Parameter(description = "Due date range end (yyyy-MM-dd, inclusive)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo,

            @Parameter(description = "Zero-based page index (default: 0)")
            @RequestParam(required = false, defaultValue = "0") Integer page,

            @Parameter(description = "Items per page, 1–100 (default: 10)")
            @RequestParam(required = false, defaultValue = "10") Integer size,

            @Parameter(description = "Sort field: id, title, status, dueDate, createdAt, updatedAt (default: createdAt)")
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction: asc or desc (default: desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {
        log.info("GET /tasks — status={}, title={}, dueDateFrom={}, dueDateTo={}, " +
                 "page={}, size={}, sortBy={}, sortDir={}",
                status, title, dueDateFrom, dueDateTo, page, size, sortBy, sortDir);

        TaskFilterRequest filter = TaskFilterRequest.builder()
                .status(status)
                .title(title)
                .dueDateFrom(dueDateFrom)
                .dueDateTo(dueDateTo)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        PagedResponse<TaskResponse> result = taskService.getFilteredTasks(filter);
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // GET /tasks/{id}
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve a single task by its ID (public endpoint)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @Parameter(description = "Task ID", required = true)
            @PathVariable Long id) {
        log.info("GET /tasks/{}", id);
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success("Task retrieved successfully", task));
    }

    // -------------------------------------------------------------------------
    // POST /tasks
    // -------------------------------------------------------------------------

    @PostMapping
    @Operation(
        summary = "Create a new task",
        description = "Create a task. Requires Bearer token authentication.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Task created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest request) {
        log.info("POST /tasks — title: {}", request.getTitle());
        TaskResponse created = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", created));
    }

    // -------------------------------------------------------------------------
    // PUT /tasks/{id}
    // -------------------------------------------------------------------------

    @PutMapping("/{id}")
    @Operation(
        summary = "Update a task",
        description = "Replace all fields of an existing task. Requires Bearer token authentication.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        log.info("PUT /tasks/{}", id);
        TaskResponse updated = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", updated));
    }

    // -------------------------------------------------------------------------
    // DELETE /tasks/{id}
    // -------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a task",
        description = "Remove a task by ID. Requires Bearer token authentication.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Task deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE /tasks/{}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // PATCH /tasks/{id}/complete
    // -------------------------------------------------------------------------

    @PatchMapping("/{id}/complete")
    @Operation(
        summary = "Mark task as completed",
        description = "Transition a task's status to COMPLETED. Requires Bearer token authentication.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task marked as completed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<ApiResponse<TaskResponse>> markTaskComplete(@PathVariable Long id) {
        log.info("PATCH /tasks/{}/complete", id);
        TaskResponse completed = taskService.markTaskComplete(id);
        return ResponseEntity.ok(ApiResponse.success("Task marked as completed", completed));
    }
}
