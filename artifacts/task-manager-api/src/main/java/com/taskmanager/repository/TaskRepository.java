package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import com.taskmanager.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Task persistence.
 * <p>
 * Extends both JpaRepository (CRUD + basic pagination) and JpaSpecificationExecutor
 * (dynamic predicate-based queries via the Specification pattern), enabling arbitrary
 * filter combinations without separate @Query methods.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    /**
     * Derived query: SELECT * FROM tasks WHERE status = ?1
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Derived query: SELECT * FROM tasks WHERE title LIKE %?1%
     */
    List<Task> findByTitleContainingIgnoreCase(String title);
}
