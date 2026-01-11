package com.bookhub.bookhub.controller;

import com.bookhub.bookhub.dto.book.request.BookCreateRequest;
import com.bookhub.bookhub.dto.book.request.BookUpdateRequest;
import com.bookhub.bookhub.dto.book.response.BookResponse;
import com.bookhub.bookhub.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieve a list of all books in the library")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "Book ID")
            @PathVariable Long id
    ) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Add a new book to the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreateRequest bookRequest) {
        BookResponse createdBook = bookService.createBook(bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Update and existing book's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book update successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<BookResponse> updateBook(
            @Parameter(description = "Book ID")
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest bookDetails) {
        BookResponse updatedBook = bookService.updateBook(id, bookDetails);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Delete a book from the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete book with active loans")
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Book ID")
            @PathVariable Long id
    ) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books by title keyword")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @Parameter(description = "Search keyword")
            @RequestParam String keyword
    ) {
        List<BookResponse> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/author/{author}")
    @Operation(summary = "Get books by author", description = "Retrieve all books by a specific author")
    @ApiResponse(responseCode = "200", description = "Books retrieve successfully")
    public ResponseEntity<List<BookResponse>> getBooksByAuthor(
            @Parameter(description = "Author name")
            @PathVariable String author
    ) {
        List<BookResponse> books = bookService.getBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "get book by ISBN", description = "Retrieve a book by its ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponse> getBookByIsbn(
            @Parameter(description = "ISBN number")
            @PathVariable String isbn
    ) {
        return bookService.getBookByIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/increment-copies")
    @Operation(summary = "Increment book copies", description = "Add more copies of a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Copies incremented successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity")
    })
    public ResponseEntity<Void> incrementCopies(
            @Parameter(description = "Book ID")
            @PathVariable Long id,
            @Parameter(description = "Number of copies to add")
            @RequestParam int quantity
    ) {
        bookService.incrementCopies(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/decrement-copies")
    @Operation(summary = "Decrement book copies", description = "Remove copies of a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Copies decremented successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity")
    })
    public ResponseEntity<Void> decrementCopies(
            @Parameter(description = "Book ID")
            @PathVariable Long id,
            @Parameter(description = "Number of copies to remove")
            @RequestParam int quantity
    ) {
        bookService.decrementCopies(id,quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/available")
    @Operation(summary = "Check book availability", description = "Check if a book has available copies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability checked"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Boolean> isBookAvailable(
            @Parameter(description = "Book ID")
            @PathVariable Long id
    ) {
        boolean isAvailable = bookService.isBookAvailable(id);
        return ResponseEntity.ok(isAvailable);
    }
}
