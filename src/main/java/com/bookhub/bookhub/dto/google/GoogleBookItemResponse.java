package com.bookhub.bookhub.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GoogleBookItemResponse {
    private String id;
    private VolumeInfo volumeInfo;

    @Data
    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;
        private String description;
        private Integer pageCount;
        private List<String> categories;
        private Double averageRating;
        private Integer ratingsCount;
        private String language;

        @JsonProperty("industryIdentifiers")
        private List<IndustryIdentifier> industryIdentifiers;

        private ImageLinks imageLinks;

        @Data
        public static class IndustryIdentifier {
            private String type;
            private String identifier;
        }

        @Data
        public static class ImageLinks {
            private String smallThumbnail;
            private String thumbnail;
        }
    }
}
