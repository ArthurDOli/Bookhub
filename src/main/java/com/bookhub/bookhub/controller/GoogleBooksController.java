package com.bookhub.bookhub.controller;

import com.bookhub.bookhub.dto.book.response.BookResponse;
import com.bookhub.bookhub.dto.google.GoogleBookItemResponse;
import com.bookhub.bookhub.service.external.GoogleBooksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/google-books")
@RequiredArgsConstructor
@Tag(name = "Google Books", description = "Endpoints for integration with the Google Books API")
public class GoogleBooksController {
    private final GoogleBooksService googleBooksService;

    @GetMapping("/search")
    @Operation(summary = "Search for books in Google Books",
            description = "Search for books using the Google Books API by search term")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "503", description = "Google Books API unavailable")
    })
    public ResponseEntity<List<GoogleBookItemResponse>> searchBooks(
            @Parameter(description = "Search query (e.g., 'java programming', 'isbn:9780134685991')")
            @RequestParam String query,
            @Parameter(description = "Maximum number of results (1-40, default: 10)")
            @RequestParam(defaultValue = "10") int maxResults) {
        List<GoogleBookItemResponse> books = googleBooksService.searchBooks(query, maxResults);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{googleBookId}")
    @Operation(summary = "Get book details from Google Books",
            description = "Retrieve detailed information about a specific book using its Google Books ID"
    )
    public ResponseEntity<GoogleBookItemResponse> getBookById(
            @Parameter(description = "Google Books ID (e.g., 'zyTCAlFPjgYC')")
            @PathVariable String googleBookId
    ) {
        GoogleBookItemResponse book = googleBooksService.getBookById(googleBookId);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/import/{googleBookId}")
    @Operation(summary = "Important book from Google Books",
            description = "Import a book from Google Books into the local library. If the book already exists (by ISBN), it increments the copy count instead."
    )
    public ResponseEntity<BookResponse> importBook(
            @Parameter(description = "Google Books ID")
            @PathVariable String googleBookId,
            @Parameter(description = "Number of copies to add (default: 1)")
            @RequestParam(defaultValue = "1") int totalCopies
    ) {
        BookResponse book = googleBooksService.importBookToLibrary(googleBookId, totalCopies);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
}
