package com.bookhub.bookhub.controller;

import com.bookhub.bookhub.dto.book.response.BookResponse;
import com.bookhub.bookhub.dto.google.GoogleBookItemResponse;
import com.bookhub.bookhub.service.external.GoogleBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/google-books")
@RequiredArgsConstructor
public class GoogleBooksController {
    private final GoogleBooksService googleBooksService;

    @GetMapping("/search")
    public ResponseEntity<List<GoogleBookItemResponse>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int maxResults) {
        List<GoogleBookItemResponse> books = googleBooksService.searchBooks(query, maxResults);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{googleBookId}")
    public ResponseEntity<GoogleBookItemResponse> getBookById(@PathVariable String googleBookId) {
        GoogleBookItemResponse book = googleBooksService.getBookById(googleBookId);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/import/{googleBookId}")
    public ResponseEntity<BookResponse> importBook(
            @PathVariable int googleBookId,
            @RequestParam(defaultValue = "1") int totalCopies
    ) {
        BookResponse book = googleBooksService.importBookToLibrary(googleBookId, totalCopies);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
}
