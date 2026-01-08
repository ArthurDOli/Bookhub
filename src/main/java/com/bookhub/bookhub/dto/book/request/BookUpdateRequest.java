package com.bookhub.bookhub.dto.book.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookUpdateRequest {
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @Size(min = 1, max = 200, message = "Author must be between 1 and 100 characters")
    private String author;

    @Min(value = 0, message = "Publication year cannot be negative")
    private Integer publicationYear;

    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;
}
