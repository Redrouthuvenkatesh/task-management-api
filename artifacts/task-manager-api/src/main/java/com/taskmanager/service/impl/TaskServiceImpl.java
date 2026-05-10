package com.taskmanager.service.impl;

import com.taskmanager.dto.PagedResponse;
import com.taskmanager.dto.TaskFilterRequest;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.service.TaskService;
import com.taskmanager.util.TaskSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Concrete implementation of {@link TaskService}.
 * <p>
 * Design decisions:
 * - @RequiredArgsConstructor enables constructor injection without boilerplate.
 * - @Transactional at class level; read-only overrides on query methods.
 * - Pagination/sort is built from the TaskFilterRequest and delegated to the
 *   JpaSpecificationExecutor, keeping business logic out of the repository.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    /**
     * Allowed sort fields — validated to prevent arbitrary field injection.
     */
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "title", "status", "dueDate", "createdAt", "updatedAt");

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        log.info("Fetching all tasks (unfiltered)");
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> getFilteredTasks(TaskFilterRequest filter) {
        log.info("Fetching tasks — status={}, title={}, dueDateFrom={}, dueDateTo={}, " +
                 "page={}, size={}, sortBy={}, sortDir={}",
                filter.getStatus(), filter.getTitle(),
                filter.getDueDateFrom(), filter.getDueDateTo(),
                filter.getPage(), filter.getSize(),
                filter.getSortBy(), filter.getSortDir());

        Pageable pageable = buildPageable(filter);
        Specification<Task> spec = TaskSpecification.fromFilter(filter);

        Page<TaskResponse> page = taskRepository
                .findAll(spec, pageable)
                .map(taskMapper::toResponse);

        log.info("Found {} task(s) matching filter (page {}/{})",
                page.getTotalElements(), page.getNumber() + 1, page.getTotalPages());

        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        log.info("Fetching task with id: {}", id);
        return taskMapper.toResponse(findTaskOrThrow(id));
    }

    // -------------------------------------------------------------------------
    // Mutations
    // -------------------------------------------------------------------------

    @Override
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creating new task with title: {}", request.getTitle());
        Task task = taskMapper.toEntity(request);
        Task saved = taskRepository.save(task);
        log.info("Task created successfully with id: {}", saved.getId());
        return taskMapper.toResponse(saved);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request) {
        log.info("Updating task with id: {}", id);
        Task task = findTaskOrThrow(id);
        taskMapper.updateEntity(task, request);
        Task updated = taskRepository.save(task);
        log.info("Task {} updated successfully", id);
        return taskMapper.toResponse(updated);
    }

    @Override
    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.delete(findTaskOrThrow(id));
        log.info("Task {} deleted successfully", id);
    }

    @Override
    public TaskResponse markTaskComplete(Long id) {
        log.info("Marking task {} as complete", id);
        Task task = findTaskOrThrow(id);
        task.setStatus(TaskStatus.COMPLETED);
        Task saved = taskRepository.save(task);
        log.info("Task {} marked as COMPLETED", id);
        return taskMapper.toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Build a {@link Pageable} from the filter DTO.
     * Falls back to "createdAt" if an unknown sort field is requested,
     * and to "desc" for an invalid sort direction.
     */
    private Pageable buildPageable(TaskFilterRequest filter) {
        int page = filter.getPage() != null ? Math.max(0, filter.getPage()) : 0;
        int size = filter.getSize() != null ? Math.min(100, Math.max(1, filter.getSize())) : 10;

        String sortField = ALLOWED_SORT_FIELDS.contains(filter.getSortBy())
                ? filter.getSortBy()
                : "createdAt";

        Sort.Direction direction = "asc".equalsIgnoreCase(filter.getSortDir())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
}
