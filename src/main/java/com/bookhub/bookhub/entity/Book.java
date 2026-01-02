package com.bookhub.bookhub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
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

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Loan> loans = new ArrayList<>();

    private Integer publicationYear;

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void borrow() {
        if (availableCopies <= 0) {
            throw new IllegalStateException("No copies available");
        }
        availableCopies--;
    }

    public void returnBook() {
        availableCopies++;
    }
}
