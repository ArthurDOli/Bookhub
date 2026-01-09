package com.bookhub.bookhub.service.external;

import com.bookhub.bookhub.dto.book.response.BookResponse;
import com.bookhub.bookhub.dto.google.GoogleBookItemResponse;
import com.bookhub.bookhub.entity.Book;
import com.bookhub.bookhub.exception.ExternalServiceException;
import com.bookhub.bookhub.exception.ResourceNotFoundException;
import com.bookhub.bookhub.factory.BookFactory;
import com.bookhub.bookhub.repository.BookRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleBooksService {
    private final RestTemplate restTemplate;
    private final BookRepository bookRepository;
    private final BookFactory bookFactory;

    @Value("${google.books.api-key}")
    private String apiKey;

    @Value("${google.books.base-url}")
    private String baseUrl;

    public List<GoogleBookItemResponse> searchBooks(String query, int maxResults) {
        try {
            String url = buildSearchUrl(query, maxResults);

            ResponseEntity<GoogleBooksSearchResponse> response = restTemplate.getForEntity(
                    url, GoogleBooksSearchResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                throw new ExternalServiceException(
                        "Google Books API returned non-OK response: " + response.getStatusCode()
                );
            }

            List<GoogleBookItemResponse> items = response.getBody().getItems();

            if (items == null) {
                return Collections.emptyList();
            }

            return items;

        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("Google Books API error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to search books: " + e.getMessage(), e);
        }
    }

    public GoogleBookItemResponse getBookById(String googleBookId) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(baseUrl + "/volumes/" + googleBookId)
                    .queryParam("key", apiKey)
                    .build()
                    .toUriString();

            ResponseEntity<GoogleBookItemResponse> response = restTemplate.getForEntity(
                    url, GoogleBookItemResponse.class
            );

            if (response.getStatusCode() != HttpStatus.OK && response.getBody() != null) {
                throw new ResourceNotFoundException("Book not found in Google Books: " + googleBookId);
            }

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Book not found in Google Books: " + googleBookId);
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("Error fetching book: " + e.getMessage());
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to fetch book: " + e.getMessage());
        }
    }

    @Transactional
    public BookResponse importBookToLibrary(String googleBookId, int totalCopies) {
        GoogleBookItemResponse googleBook = getBookById(googleBookId);
        GoogleBookItemResponse.VolumeInfo volumeInfo = googleBook.getVolumeInfo();

        String isbn = extractIsbn(volumeInfo);

        if (isbn != null && bookRepository.findById(isbn).isPresent()) {
            Book existingBook = bookRepository.findByIsbn(isbn).get();
            existingBook.setTotalCopies(existingBook.getTotalCopies() + totalCopies);
            existingBook.setAvailableCopies(existingBook.getAvailableCopies() + totalCopies);
            Book updated = bookRepository.save(existingBook);
            return new BookResponse(updated);
        }

        String title = volumeInfo.getTitle();
        String author = extractAuthors(volumeInfo);
        Integer year = extractPublicationYear(volumeInfo);

        Book book = bookFactory.createBookWithCopies(title, author, isbn, year, totalCopies);
        Book saved = bookRepository.save(book);

        return new BookResponse(saved);
    }

    private String extractIsbn(GoogleBookItemResponse.VolumeInfo volumeInfo) {
        if (volumeInfo.getIndustryIdentifiers() == null) {
            return null;
        }

        for (GoogleBookItemResponse.VolumeInfo.IndustryIdentifier id : volumeInfo.getIndustryIdentifiers()) {
            if ("ISBN_13".equals(id.getType())) {
                return id.getIdentifier();
            }
        }

        for (GoogleBookItemResponse.VolumeInfo.IndustryIdentifier id : volumeInfo.getIndustryIdentifiers()) {
            if ("ISBN_10".equals(id.getType())) {
                return id.getIdentifier();
            }
        }

        return null;
    }

    private String extractAuthors(GoogleBookItemResponse.VolumeInfo volumeInfo) {
        if (volumeInfo.getAuthors() == null || volumeInfo.getAuthors().isEmpty()) {
            return "Unknown Author";
        }

        return String.join(", ", volumeInfo.getAuthors());
    }

    private Integer extractPublicationYear(GoogleBookItemResponse.VolumeInfo volumeInfo) {
        String publishedDate = volumeInfo.getPublishedDate();

        if (publishedDate == null || publishedDate.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(publishedDate.substring(0, Math.min(4, publishedDate.length())));
        } catch (Exception e) {
            return null;
        }
    }

    private String buildSearchUrl(String query, int maxResults) {
        return UriComponentsBuilder.fromUriString(baseUrl + "/volumes")
                .queryParam("q", query)
                .queryParam("maxResults", Math.min(maxResults, 40))
                .queryParam("key", apiKey)
                .queryParam("printType", "books")
                .build()
                .toUriString();
    }

    @Data
    private static class GoogleBooksSearchResponse {
        private List<GoogleBookItemResponse> items;
    }
}
