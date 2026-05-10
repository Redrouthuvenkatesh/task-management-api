package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.exception.TaskNotFoundException;
import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TaskServiceImpl}.
 * <p>
 * All external collaborators (TaskRepository, TaskMapper) are mocked via Mockito.
 * Tests follow the Arrange / Act / Assert (AAA) pattern and use AssertJ for
 * expressive, readable assertions.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    // -------------------------------------------------------------------------
    // Shared test fixtures
    // -------------------------------------------------------------------------

    private Task sampleTask;
    private TaskResponse sampleResponse;
    private TaskRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("A sample task for testing")
                .dueDate(LocalDate.now().plusDays(7))
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleResponse = TaskResponse.builder()
                .id(1L)
                .title("Test Task")
                .description("A sample task for testing")
                .dueDate(LocalDate.now().plusDays(7))
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = TaskRequest.builder()
                .title("Test Task")
                .description("A sample task for testing")
                .dueDate(LocalDate.now().plusDays(7))
                .build();
    }

    // -------------------------------------------------------------------------
    // getAllTasks()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getAllTasks()")
    class GetAllTasks {

        @Test
        @DisplayName("should return all tasks as response DTOs")
        void shouldReturnAllTasks() {
            when(taskRepository.findAll()).thenReturn(List.of(sampleTask));
            when(taskMapper.toResponse(sampleTask)).thenReturn(sampleResponse);

            List<TaskResponse> result = taskService.getAllTasks();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
            verify(taskRepository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no tasks exist")
        void shouldReturnEmptyList() {
            when(taskRepository.findAll()).thenReturn(List.of());

            List<TaskResponse> result = taskService.getAllTasks();

            assertThat(result).isEmpty();
            verify(taskRepository).findAll();
        }
    }

    // -------------------------------------------------------------------------
    // getTaskById()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getTaskById()")
    class GetTaskById {

        @Test
        @DisplayName("should return task response when task exists")
        void shouldReturnTaskWhenFound() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
            when(taskMapper.toResponse(sampleTask)).thenReturn(sampleResponse);

            TaskResponse result = taskService.getTaskById(1L);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Task");
        }

        @Test
        @DisplayName("should throw TaskNotFoundException when task does not exist")
        void shouldThrowWhenNotFound() {
            when(taskRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.getTaskById(99L))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // -------------------------------------------------------------------------
    // createTask()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("createTask()")
    class CreateTask {

        @Test
        @DisplayName("should create and return new task with PENDING status")
        void shouldCreateTaskSuccessfully() {
            when(taskMapper.toEntity(sampleRequest)).thenReturn(sampleTask);
            when(taskRepository.save(sampleTask)).thenReturn(sampleTask);
            when(taskMapper.toResponse(sampleTask)).thenReturn(sampleResponse);

            TaskResponse result = taskService.createTask(sampleRequest);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Task");
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("should invoke mapper.toEntity and repository.save exactly once")
        void shouldInvokeCollaboratorsOnce() {
            when(taskMapper.toEntity(sampleRequest)).thenReturn(sampleTask);
            when(taskRepository.save(sampleTask)).thenReturn(sampleTask);
            when(taskMapper.toResponse(sampleTask)).thenReturn(sampleResponse);

            taskService.createTask(sampleRequest);

            verify(taskMapper, times(1)).toEntity(sampleRequest);
            verify(taskRepository, times(1)).save(sampleTask);
            verify(taskMapper, times(1)).toResponse(sampleTask);
        }
    }

    // -------------------------------------------------------------------------
    // updateTask()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("updateTask()")
    class UpdateTask {

        @Test
        @DisplayName("should update task fields and return updated response")
        void shouldUpdateTaskSuccessfully() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
            when(taskRepository.save(sampleTask)).thenReturn(sampleTask);
            when(taskMapper.toResponse(sampleTask)).thenReturn(sampleResponse);

            TaskResponse result = taskService.updateTask(1L, sampleRequest);

            assertThat(result).isNotNull();
            verify(taskMapper).updateEntity(sampleTask, sampleRequest);
            verify(taskRepository).save(sampleTask);
        }

        @Test
        @DisplayName("should throw TaskNotFoundException when updating non-existent task")
        void shouldThrowWhenTaskNotFound() {
            when(taskRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.updateTask(99L, sampleRequest))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessageContaining("99");

            verify(taskRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // deleteTask()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("deleteTask()")
    class DeleteTask {

        @Test
        @DisplayName("should delete task without returning a value")
        void shouldDeleteTaskSuccessfully() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

            assertThatCode(() -> taskService.deleteTask(1L)).doesNotThrowAnyException();

            verify(taskRepository).delete(sampleTask);
        }

        @Test
        @DisplayName("should throw TaskNotFoundException when deleting non-existent task")
        void shouldThrowWhenTaskNotFound() {
            when(taskRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.deleteTask(99L))
                    .isInstanceOf(TaskNotFoundException.class);

            verify(taskRepository, never()).delete(any());
        }
    }

    // -------------------------------------------------------------------------
    // markTaskComplete()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("markTaskComplete()")
    class MarkTaskComplete {

        @Test
        @DisplayName("should set task status to COMPLETED")
        void shouldMarkTaskAsCompleted() {
            // Arrange: task starts as PENDING
            sampleTask.setStatus(TaskStatus.PENDING);
            TaskResponse completedResponse = TaskResponse.builder()
                    .id(1L)
                    .title("Test Task")
                    .status(TaskStatus.COMPLETED)
                    .build();

            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
            when(taskRepository.save(sampleTask)).thenReturn(sampleTask);
            when(taskMapper.toResponse(sampleTask)).thenReturn(completedResponse);

            // Act
            TaskResponse result = taskService.markTaskComplete(1L);

            // Assert
            assertThat(sampleTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
            assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
            verify(taskRepository).save(sampleTask);
        }

        @Test
        @DisplayName("should throw TaskNotFoundException when task does not exist")
        void shouldThrowWhenTaskNotFound() {
            when(taskRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.markTaskComplete(99L))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessageContaining("99");

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("should be idempotent — completing an already-COMPLETED task is allowed")
        void shouldBeIdempotentForCompletedTask() {
            sampleTask.setStatus(TaskStatus.COMPLETED);
            TaskResponse completedResponse = TaskResponse.builder()
                    .id(1L)
                    .title("Test Task")
                    .status(TaskStatus.COMPLETED)
                    .build();

            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
            when(taskRepository.save(sampleTask)).thenReturn(sampleTask);
            when(taskMapper.toResponse(sampleTask)).thenReturn(completedResponse);

            TaskResponse result = taskService.markTaskComplete(1L);

            assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        }
    }
}
