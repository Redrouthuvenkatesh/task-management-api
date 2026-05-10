package com.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Generic paginated response envelope used for list endpoints that support paging.
 * <p>
 * Design decision: embedding pagination metadata (page, size, totalElements, totalPages,
 * first, last) directly in the response body avoids clients needing to inspect headers,
 * which is more idiomatic for REST JSON APIs.
 *
 * @param <T> the type of content items in this page
 */
@Value
@Builder
public class PagedResponse<T> {

    List<T> content;

    int page;
    int size;
    long totalElements;
    int totalPages;
    boolean first;
    boolean last;
    boolean empty;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Convenience factory: construct from a Spring {@link Page} slice.
     */
    public static <T> PagedResponse<T> of(Page<T> pageSlice) {
        return PagedResponse.<T>builder()
                .content(pageSlice.getContent())
                .page(pageSlice.getNumber())
                .size(pageSlice.getSize())
                .totalElements(pageSlice.getTotalElements())
                .totalPages(pageSlice.getTotalPages())
                .first(pageSlice.isFirst())
                .last(pageSlice.isLast())
                .empty(pageSlice.isEmpty())
                .build();
    }
}
