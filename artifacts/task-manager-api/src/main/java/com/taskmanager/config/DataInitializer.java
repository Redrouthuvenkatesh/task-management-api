package com.taskmanager.config;

import com.taskmanager.entity.Task;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

/**
 * Seeds the H2 in-memory database with sample tasks on startup.
 * <p>
 * Design decision: CommandLineRunner runs after the ApplicationContext is fully
 * initialized, so all repositories and services are ready. This approach is
 * simpler than data.sql for complex, code-driven seed data.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(TaskRepository taskRepository) {
        return args -> {
            if (taskRepository.count() > 0) {
                log.info("Database already seeded — skipping initialization");
                return;
            }

            log.info("Seeding database with sample tasks...");

            taskRepository.save(Task.builder()
                    .title("Set up project infrastructure")
                    .description("Configure CI/CD pipeline, Docker, and cloud deployment.")
                    .dueDate(LocalDate.now().plusDays(7))
                    .status(TaskStatus.COMPLETED)
                    .build());

            taskRepository.save(Task.builder()
                    .title("Design REST API contracts")
                    .description("Define OpenAPI specification for all endpoints.")
                    .dueDate(LocalDate.now().plusDays(14))
                    .status(TaskStatus.IN_PROGRESS)
                    .build());

            taskRepository.save(Task.builder()
                    .title("Implement authentication module")
                    .description("JWT-based authentication with refresh token support. Max description is 500 chars.")
                    .dueDate(LocalDate.now().plusDays(21))
                    .status(TaskStatus.PENDING)
                    .build());

            taskRepository.save(Task.builder()
                    .title("Write unit and integration tests")
                    .description("Achieve 80%+ code coverage using JUnit 5 and Mockito.")
                    .dueDate(LocalDate.now().plusDays(28))
                    .status(TaskStatus.PENDING)
                    .build());

            taskRepository.save(Task.builder()
                    .title("Deploy to production")
                    .description("Deploy the application to AWS ECS with RDS PostgreSQL backend.")
                    .dueDate(LocalDate.now().plusDays(35))
                    .status(TaskStatus.PENDING)
                    .build());

            log.info("Database seeded successfully with {} tasks", taskRepository.count());
        };
    }
}
