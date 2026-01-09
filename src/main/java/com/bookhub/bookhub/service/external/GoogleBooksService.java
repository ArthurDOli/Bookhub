package com.bookhub.bookhub.service.external;

import com.bookhub.bookhub.dto.google.GoogleBookItemResponse;
import com.bookhub.bookhub.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleBooksService {
    private final RestTemplate restTemplate;

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
                return response.getBody().getItems();
            }

            throw new ExternalServiceException(
                    "Google Books API returned non-OK response: " + response.getStatusCode()
            );
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("Google Books API error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to search books: " + e.getMessage(), e);
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

    private static class GoogleBooksSearchResponse {
        private List<GoogleBookItemResponse> items;

        public List<GoogleBookItemResponse> getItems() {
            return items;
        }

        public void setItems(List<GoogleBookItemResponse> items) {
            this.items = items;
        }
    }
}
