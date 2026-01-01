package com.bookhub.bookhub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(columnDefinition = "integer default 1")
    private Integer totalCopies;

    @Column(columnDefinition = "integer default 1")
    private Integer availableCopies;

    private Integer publicationYear;
}
