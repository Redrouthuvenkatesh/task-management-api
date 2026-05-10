package com.taskmanager.util;

import com.taskmanager.dto.TaskFilterRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.enums.TaskStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification factory for dynamic Task query construction.
 * <p>
 * Design decision: using the Specification pattern allows arbitrary combinations of
 * filter predicates without needing a separate @Query method for every combination.
 * Each active filter adds a predicate to the AND chain; inactive filters (null) are
 * simply skipped. This keeps the repository interface thin.
 */
public final class TaskSpecification {

    private TaskSpecification() {
        // Utility class — not instantiable
    }

    /**
     * Build a {@link Specification<Task>} from a {@link TaskFilterRequest}.
     * Only non-null filter fields produce predicates.
     *
     * @param filter the filter criteria supplied by the caller
     * @return a composed AND-predicate Specification
     */
    public static Specification<Task> fromFilter(TaskFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // --- status filter ---
            TaskStatus status = filter.getStatus();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // --- title partial match (case-insensitive) ---
            String title = filter.getTitle();
            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("title")),
                        "%" + title.trim().toLowerCase() + "%"
                ));
            }

            // --- due date range: from ---
            LocalDate dueDateFrom = filter.getDueDateFrom();
            if (dueDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateFrom));
            }

            // --- due date range: to ---
            LocalDate dueDateTo = filter.getDueDateTo();
            if (dueDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueDateTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
